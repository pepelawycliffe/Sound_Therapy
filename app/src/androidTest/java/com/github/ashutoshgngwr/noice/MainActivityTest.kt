package com.github.ashutoshgngwr.noice

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ashutoshgngwr.noice.fragment.AboutFragment
import com.github.ashutoshgngwr.noice.fragment.PresetFragment
import com.github.ashutoshgngwr.noice.fragment.SleepTimerFragment
import com.github.ashutoshgngwr.noice.fragment.SoundLibraryFragment
import com.github.ashutoshgngwr.noice.fragment.SupportDevelopmentFragment
import com.github.ashutoshgngwr.noice.fragment.WakeUpTimerFragment
import com.github.ashutoshgngwr.noice.sound.player.PlayerManager
import com.google.android.material.navigation.NavigationView
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  @Rule
  @JvmField
  val retryTestRule = RetryTestRule(5)

  private lateinit var activityScenario: ActivityScenario<MainActivity>

  @Before
  fun setup() {
    // mark app intro as seen to run main activity tests in peace
    PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
      .edit(commit = true) {
        putBoolean(AppIntroActivity.PREF_HAS_USER_SEEN_APP_INTRO, true)
      }

    activityScenario = launch(MainActivity::class.java)
  }

  @After
  fun teardown() {
    activityScenario.close()
    unmockkAll()

    PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
      .edit(commit = true) { clear() }
  }

  @Test
  fun testIfSoundLibraryIsVisibleOnStart() {
    activityScenario.onActivity {
      assertEquals(
        SoundLibraryFragment::class.java.simpleName,
        it.supportFragmentManager.getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1).name
      )

      assertEquals(
        R.id.library,
        it.findViewById<NavigationView>(R.id.navigation_drawer).checkedItem?.itemId
      )
    }
  }

  @Test
  fun testIfSavedPresetsIsVisibleOnStart() {
    mockkObject(PresetFragment.Companion)
    every { PresetFragment.shouldDisplayAsHomeScreen(any()) } returns true

    // recreate activity with null bundle
    activityScenario.close()
    activityScenario = launch(MainActivity::class.java)
    activityScenario.onActivity {
      assertEquals(
        PresetFragment::class.java.simpleName,
        it.supportFragmentManager.getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1).name
      )

      assertEquals(
        R.id.saved_presets,
        it.findViewById<NavigationView>(R.id.navigation_drawer).checkedItem?.itemId
      )
    }
  }

  @Test
  fun testSavedPresetMenuItem() {
    onView(withId(R.id.layout_main))
      .check(matches(isClosed(Gravity.START)))
      .perform(DrawerActions.open(Gravity.START))

    onView(withId(R.id.navigation_drawer))
      .perform(NavigationViewActions.navigateTo(R.id.saved_presets))

    activityScenario.onActivity {
      assertEquals(
        PresetFragment::class.java.simpleName,
        it.supportFragmentManager.getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1).name
      )

      it.findViewById<NavigationView>(R.id.navigation_drawer).also { navView ->
        assertEquals(R.id.saved_presets, navView.checkedItem?.itemId)
        assertEquals(it.getString(R.string.saved_presets), navView.checkedItem?.title)
      }
    }
  }

  @Test
  fun testSleepTimerMenuItem() {
    onView(withId(R.id.layout_main))
      .check(matches(isClosed(Gravity.START)))
      .perform(DrawerActions.open(Gravity.START))

    onView(withId(R.id.navigation_drawer))
      .perform(NavigationViewActions.navigateTo(R.id.sleep_timer))

    activityScenario.onActivity {
      assertEquals(
        SleepTimerFragment::class.java.simpleName,
        it.supportFragmentManager.getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1).name
      )

      it.findViewById<NavigationView>(R.id.navigation_drawer).also { navView ->
        assertEquals(R.id.sleep_timer, navView.checkedItem?.itemId)
        assertEquals(it.getString(R.string.sleep_timer), navView.checkedItem?.title)
      }
    }
  }

  @Test
  fun testWakeUpTimerMenuItem() {
    onView(withId(R.id.layout_main))
      .check(matches(isClosed(Gravity.START)))
      .perform(DrawerActions.open(Gravity.START))

    onView(withId(R.id.navigation_drawer))
      .perform(NavigationViewActions.navigateTo(R.id.wake_up_timer))

    activityScenario.onActivity {
      assertEquals(
        WakeUpTimerFragment::class.java.simpleName,
        it.supportFragmentManager.getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1).name
      )

      it.findViewById<NavigationView>(R.id.navigation_drawer).also { navView ->
        assertEquals(R.id.wake_up_timer, navView.checkedItem?.itemId)
        assertEquals(it.getString(R.string.wake_up_timer), navView.checkedItem?.title)
      }
    }
  }

  @Test
  fun testThemeMenuItem() {
    val nightModes = arrayOf(
      AppCompatDelegate.MODE_NIGHT_NO,
      AppCompatDelegate.MODE_NIGHT_YES,
      AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

    val themes = InstrumentationRegistry.getInstrumentation()
      .targetContext
      .resources
      .getStringArray(R.array.app_themes)

    for (i in themes.indices) {
      onView(withId(R.id.layout_main))
        .check(matches(isClosed(Gravity.START)))
        .perform(DrawerActions.open(Gravity.START))

      onView(withId(R.id.navigation_drawer))
        .perform(NavigationViewActions.navigateTo(R.id.app_theme))

      onView(withText(themes[i]))
        .inRoot(isDialog())
        .check(matches(isDisplayed()))
        .perform(click())

      onView(withId(R.id.layout_main)).check(matches(isDisplayed())) // wait for activity to recreate
      assertEquals(nightModes[i], AppCompatDelegate.getDefaultNightMode())
    }
  }

  @Test
  fun testAboutMenuItem() {
    onView(withId(R.id.layout_main))
      .check(matches(isClosed(Gravity.START)))
      .perform(DrawerActions.open(Gravity.START))

    onView(withId(R.id.navigation_drawer))
      .perform(NavigationViewActions.navigateTo(R.id.about))

    activityScenario.onActivity {
      assertEquals(
        AboutFragment::class.java.simpleName,
        it.supportFragmentManager.getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1).name
      )

      it.findViewById<NavigationView>(R.id.navigation_drawer).also { navView ->
        assertEquals(R.id.about, navView.checkedItem?.itemId)
        assertEquals(it.getString(R.string.about), navView.checkedItem?.title)
      }
    }
  }

  @Test
  fun testHelpMenuItem() {
    Intents.init()
    try {
      onView(withId(R.id.layout_main))
        .check(matches(isClosed(Gravity.START)))
        .perform(DrawerActions.open(Gravity.START))

      onView(withId(R.id.navigation_drawer))
        .perform(NavigationViewActions.navigateTo(R.id.help))

      intended(hasComponent(AppIntroActivity::class.qualifiedName))
    } finally {
      Intents.release()
    }
  }

  @Test
  fun testSupportDevelopmentMenuItem() {
    onView(withId(R.id.layout_main))
      .check(matches(isClosed(Gravity.START)))
      .perform(DrawerActions.open(Gravity.START))

    onView(withId(R.id.navigation_drawer))
      .perform(NavigationViewActions.navigateTo(R.id.support_development))

    activityScenario.onActivity {
      assertEquals(
        SupportDevelopmentFragment::class.java.simpleName,
        it.supportFragmentManager.getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1).name
      )

      it.findViewById<NavigationView>(R.id.navigation_drawer).also { navView ->
        assertEquals(R.id.support_development, navView.checkedItem?.itemId)
        assertEquals(it.getString(R.string.support_development), navView.checkedItem?.title)
      }
    }
  }

  @Test
  fun testReportIssuesMenuItem() {
    Intents.init()
    try {
      Intents.intending(hasAction(Intent.ACTION_VIEW))
        .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, Intent()))

      onView(withId(R.id.layout_main))
        .check(matches(isClosed(Gravity.START)))
        .perform(DrawerActions.open(Gravity.START))

      onView(withId(R.id.navigation_drawer))
        .perform(NavigationViewActions.navigateTo(R.id.report_issue))

      intended(
        filterEquals(
          Intent(
            Intent.ACTION_VIEW, Uri.parse(
              InstrumentationRegistry.getInstrumentation()
                .targetContext
                .getString(R.string.app_issues_url)
            )
          )
        )
      )
    } finally {
      Intents.release()
    }
  }

  @Test
  fun testSubmitFeedbackMenuItem() {
    Intents.init()
    try {
      Intents.intending(hasAction(Intent.ACTION_VIEW))
        .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, Intent()))

      onView(withId(R.id.layout_main))
        .check(matches(isClosed(Gravity.START)))
        .perform(DrawerActions.open(Gravity.START))

      onView(withId(R.id.navigation_drawer))
        .perform(NavigationViewActions.navigateTo(R.id.submit_feedback))

      intended(
        filterEquals(
          Intent(
            Intent.ACTION_VIEW, Uri.parse(
              InstrumentationRegistry.getInstrumentation()
                .targetContext
                .getString(R.string.feedback_form_url)
            )
          )
        )
      )
    } finally {
      Intents.release()
    }
  }

  @Test
  fun testRateOnPlayStoreMenuItem() {
    Intents.init()
    try {
      Intents.intending(hasAction(Intent.ACTION_VIEW))
        .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, Intent()))

      onView(withId(R.id.layout_main))
        .check(matches(isClosed(Gravity.START)))
        .perform(DrawerActions.open(Gravity.START))

      onView(withId(R.id.navigation_drawer))
        .perform(NavigationViewActions.navigateTo(R.id.rate_on_play_store))

      intended(
        filterEquals(
          Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
              ApplicationProvider.getApplicationContext<Context>()
                .getString(R.string.rate_us_on_play_store_url)
            )
          )
        )
      )
    } finally {
      Intents.release()
    }
  }

  @Test
  fun testBackNavigation() {
    onView(withId(R.id.layout_main))
      .check(matches(isClosed(Gravity.START)))
      .perform(DrawerActions.open(Gravity.START))

    onView(withId(R.id.navigation_drawer))
      .perform(NavigationViewActions.navigateTo(R.id.saved_presets))

    EspressoX.waitForView(allOf(withId(R.id.layout_main), isClosed(Gravity.START)))
      .perform(DrawerActions.open(Gravity.START))

    onView(withId(R.id.navigation_drawer))
      .perform(NavigationViewActions.navigateTo(R.id.about))

    EspressoX.waitForView(allOf(withId(R.id.layout_main), isClosed(Gravity.START)))
      .check(matches(isClosed(Gravity.START)))

    activityScenario.onActivity {
      it.onBackPressed()
      assertEquals(
        PresetFragment::class.java.simpleName,
        it.supportFragmentManager
          .getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1)
          .name
      )

      it.onBackPressed()
      assertEquals(
        SoundLibraryFragment::class.java.simpleName,
        it.supportFragmentManager
          .getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1)
          .name
      )

      // ensure that fragments are not present in the back stack
      assertFalse(
        it.supportFragmentManager.popBackStackImmediate(
          AboutFragment::class.java.simpleName,
          FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
      )

      assertFalse(
        it.supportFragmentManager.popBackStackImmediate(
          PresetFragment::class.java.simpleName,
          FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
      )

      it.onBackPressed()
      assertTrue(it.isFinishing || it.isDestroyed)
    }
  }

  @Test
  fun testReClickingNavigationItemForFragments() {
    // should have just 1 entry back stack
    activityScenario.onActivity {
      assertEquals(1, it.supportFragmentManager.backStackEntryCount)
    }

    // open nav drawer
    onView(withId(R.id.layout_main))
      .check(matches(isClosed(Gravity.START)))
      .perform(DrawerActions.open(Gravity.START))

    // open a new fragment using the menu item
    onView(withId(R.id.navigation_drawer)).perform(NavigationViewActions.navigateTo(R.id.about))
    activityScenario.onActivity {
      assertEquals(2, it.supportFragmentManager.backStackEntryCount)
    }

    EspressoX.waitForView(allOf(withId(R.id.layout_main), isClosed(Gravity.START)))
      .perform(DrawerActions.open(Gravity.START))

    // try to reselect the same nav item
    onView(withId(R.id.navigation_drawer)).perform(NavigationViewActions.navigateTo(R.id.about))
    activityScenario.onActivity {
      // back stack entry count should remain the same
      assertEquals(2, it.supportFragmentManager.backStackEntryCount)
    }
  }

  @Test
  fun testPlayPauseToggleMenuItem() {
    mockkObject(MediaPlayerService.Companion)
    activityScenario.onActivity {
      it.onPlayerManagerUpdate(mockk(relaxed = true) {
        every { state } returns PlayerManager.State.STOPPED
      })
    }

    // shouldn't be displayed by default
    onView(withId(R.id.action_play_pause_toggle)).check(doesNotExist())

    // with ongoing playback
    activityScenario.onActivity {
      it.onPlayerManagerUpdate(mockk(relaxed = true) {
        every { state } returns PlayerManager.State.PLAYING
      })
    }

    EspressoX.waitForView(withId(R.id.action_play_pause_toggle))
      .check(matches(isDisplayed()))
      .perform(click())

    verify(exactly = 1) { MediaPlayerService.pausePlayback(any()) }

    // with paused playback
    activityScenario.onActivity {
      it.onPlayerManagerUpdate(mockk(relaxed = true) {
        every { state } returns PlayerManager.State.PAUSED
      })
    }

    EspressoX.waitForView(withId(R.id.action_play_pause_toggle))
      .check(matches(isDisplayed()))
      .perform(click())

    verify(exactly = 1) { MediaPlayerService.resumePlayback(any()) }

    // with stopped playback
    activityScenario.onActivity {
      it.onPlayerManagerUpdate(mockk(relaxed = true) {
        every { state } returns PlayerManager.State.STOPPED
      })
    }

    EspressoX.waitForView(withId(R.id.action_play_pause_toggle))
      .check(doesNotExist())
  }

  @Test
  fun testNavigatedFragmentIntentExtra() {
    activityScenario.close()
    activityScenario = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
      .let {
        it.putExtra(MainActivity.EXTRA_CURRENT_NAVIGATED_FRAGMENT, R.id.about)
        launch(it)
      }

    activityScenario.onActivity {
      assertEquals(
        AboutFragment::class.simpleName,
        it.supportFragmentManager
          .getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1)
          .name
      )
    }
  }
}
