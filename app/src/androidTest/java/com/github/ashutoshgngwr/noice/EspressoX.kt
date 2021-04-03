package com.github.ashutoshgngwr.noice

import android.view.View
import android.widget.CompoundButton
import android.widget.TimePicker
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.TreeIterables
import com.github.ashutoshgngwr.noice.widget.DurationPicker
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.TypeSafeMatcher

/**
 * [EspressoX] contains the custom extended util implementations for Espresso.
 */
object EspressoX {

  /**
   * [clickInItem] performs a click action on item with the given [viewId] inside currently
   * matched view.
   */
  fun clickInItem(@IdRes viewId: Int): ViewAction {
    return object : ViewAction {
      override fun getDescription() = "Click on view with specified id"
      override fun getConstraints() = hasDescendant(withId(viewId))

      override fun perform(uiController: UiController, view: View) {
        view.findViewById<View>(viewId).also { it.performClick() }
      }
    }
  }

  /**
   * [slide] emulates slide action on the provided [Slider] widget.
   */
  fun slide(value: Float): ViewAction {
    return object : ViewAction {
      override fun getDescription() = "Emulate user input on a slider"
      override fun getConstraints() = instanceOf<View>(Slider::class.java)

      override fun perform(uiController: UiController, view: View) {
        view as Slider
        val height = view.height - view.paddingTop - view.paddingBottom
        val location = intArrayOf(0, 0)
        view.getLocationOnScreen(location)

        val xOffset = location[0].toFloat() + view.paddingStart + view.trackSidePadding
        val range = view.valueTo - view.valueFrom
        val xStart = (((view.value - view.valueFrom) / range) * view.trackWidth) + xOffset

        val x = (((value - view.valueFrom) / range) * view.trackWidth) + xOffset
        val y = location[1] + view.paddingTop + (height.toFloat() / 2)

        val startCoordinates = floatArrayOf(xStart, y)
        val endCoordinates = floatArrayOf(x, y)
        val precision = floatArrayOf(1f, 1f)

        // Send down event, and send up
        val down = MotionEvents.sendDown(uiController, startCoordinates, precision).down
        uiController.loopMainThreadForAtLeast(100)
        MotionEvents.sendMovement(uiController, down, endCoordinates)
        uiController.loopMainThreadForAtLeast(100)
        MotionEvents.sendUp(uiController, down, endCoordinates)
      }
    }
  }

  /**
   * [slideInItem] emulates a slide action on a [Slider] with given [sliderID] that is a descendant
   * of the provided view.
   */
  fun slideInItem(@IdRes sliderID: Int, value: Float): ViewAction {
    return object : ViewAction {
      override fun getDescription() = "Emulate user input on a descendant slider"
      override fun getConstraints() =
        hasDescendant(allOf(withId(sliderID), instanceOf(Slider::class.java)))

      override fun perform(uiController: UiController, view: View) {
        slide(value).perform(uiController, view.findViewById<Slider>(sliderID))
      }
    }
  }

  /**
   * [withSliderValue] matches a [Slider] with the provided [expectedValue].
   */
  fun withSliderValue(expectedValue: Float): Matcher<View> {
    return object : BoundedMatcher<View, Slider>(Slider::class.java) {
      override fun describeTo(description: Description) {
        description.appendText("Slider with value $expectedValue")
      }

      override fun matchesSafely(slider: Slider): Boolean {
        return expectedValue == slider.value
      }
    }
  }

  private fun searchForView(viewMatcher: Matcher<View>): ViewAction {
    return object : ViewAction {
      override fun getConstraints() = isRoot()
      override fun getDescription() = "search for view with $viewMatcher in the root view"

      override fun perform(uiController: UiController, view: View) {
        TreeIterables.breadthFirstViewTraversal(view).forEach {
          if (viewMatcher.matches(it)) {
            return
          }
        }

        throw NoMatchingViewException.Builder()
          .withRootView(view)
          .withViewMatcher(viewMatcher)
          .build()
      }
    }
  }

  /**
   * [waitForView] tries to find a view with given [viewMatcher]. If found, it returns the
   * [ViewInteraction] for the given [viewMatcher]. If not found, it waits for given [wait]
   * before attempting to find the view again. It reties for given number of [retries].
   *
   * Adaptation of the [StackOverflow post by manbradcalf](https://stackoverflow.com/a/56499223/2410641)
   */
  fun waitForView(viewMatcher: Matcher<View>, retries: Int = 5, wait: Long = 250): ViewInteraction {
    require(retries > 0 && wait > 0)
    for (i in 0 until retries) {
      try {
        onView(isRoot()).perform(searchForView(viewMatcher))
        return onView(viewMatcher)
      } catch (e: NoMatchingViewException) {
        if (i == retries) {
          throw e
        }

        Thread.sleep(wait)
      }
    }

    return onView(viewMatcher)
  }

  /**
   * Returns a [ViewAction] that invokes [DurationPicker.onDurationAddedListener] with the given
   * [durationSecs].
   */
  fun addDurationToPicker(durationSecs: Long): ViewAction {
    return object : ViewAction {
      override fun getDescription() = "add duration to a DurationPicker"
      override fun getConstraints() = instanceOf<View>(DurationPicker::class.java)

      override fun perform(uiController: UiController, view: View) {
        view as DurationPicker
        view.invokeOnDurationAddedListener(durationSecs * 1000L)
      }
    }
  }

  /**
   * Returns a [Matcher] that matches reset button of the [DurationPicker] view.
   */
  fun withDurationPickerResetButton(durationPickerMatcher: Matcher<View>): Matcher<View> {
    return allOf(isDescendantOfA(durationPickerMatcher), withId(R.id.reset_button))
  }

  /**
   * [withErrorText] matches [TextInputLayout]s using the provided error text.
   */
  fun withErrorText(@StringRes expectedErrorText: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
      override fun describeTo(description: Description?) = Unit
      override fun matchesSafely(item: View?): Boolean {
        if (item !is TextInputLayout) return false
        val error = item.error ?: return false
        return item.context.getString(expectedErrorText) == error.toString()
      }
    }
  }

  /**
   * [is24hViewEnabled] matches a [TimePicker] that has the field [TimePicker.is24HourView] enabled.
   */
  fun is24hViewEnabled(): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
      override fun describeTo(description: Description?) = Unit
      override fun matchesSafely(item: View?): Boolean {
        return item is TimePicker && item.is24HourView
      }
    }
  }

  /**
   * [setChecked] returns a [ViewAction] to set the checked state of a [CompoundButton].
   */
  fun setChecked(checked: Boolean): ViewAction {
    return object : ViewAction {
      override fun getDescription() = "check/uncheck compound buttons"
      override fun getConstraints() = instanceOf<View>(CompoundButton::class.java)

      override fun perform(uiController: UiController, view: View) {
        if (view is CompoundButton) {
          view.isChecked = checked
        }
      }
    }
  }
}
