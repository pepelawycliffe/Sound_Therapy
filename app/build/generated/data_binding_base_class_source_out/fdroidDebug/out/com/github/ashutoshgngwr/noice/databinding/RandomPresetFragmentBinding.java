// Generated by view binder compiler. Do not edit!
package com.github.ashutoshgngwr.noice.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.github.ashutoshgngwr.noice.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hopenlib.flextools.FlexRadioGroup;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RandomPresetFragmentBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final FloatingActionButton playPresetButton;

  @NonNull
  public final FlexRadioGroup presetIntensity;

  @NonNull
  public final RadioButton presetIntensityAny;

  @NonNull
  public final RadioButton presetIntensityDense;

  @NonNull
  public final RadioButton presetIntensityLight;

  @NonNull
  public final ScrollView presetRandomScrollview;

  @NonNull
  public final FlexRadioGroup presetType;

  @NonNull
  public final RadioButton presetTypeAny;

  @NonNull
  public final RadioButton presetTypeFocus;

  @NonNull
  public final RadioButton presetTypeRelax;

  private RandomPresetFragmentBinding(@NonNull ConstraintLayout rootView,
      @NonNull FloatingActionButton playPresetButton, @NonNull FlexRadioGroup presetIntensity,
      @NonNull RadioButton presetIntensityAny, @NonNull RadioButton presetIntensityDense,
      @NonNull RadioButton presetIntensityLight, @NonNull ScrollView presetRandomScrollview,
      @NonNull FlexRadioGroup presetType, @NonNull RadioButton presetTypeAny,
      @NonNull RadioButton presetTypeFocus, @NonNull RadioButton presetTypeRelax) {
    this.rootView = rootView;
    this.playPresetButton = playPresetButton;
    this.presetIntensity = presetIntensity;
    this.presetIntensityAny = presetIntensityAny;
    this.presetIntensityDense = presetIntensityDense;
    this.presetIntensityLight = presetIntensityLight;
    this.presetRandomScrollview = presetRandomScrollview;
    this.presetType = presetType;
    this.presetTypeAny = presetTypeAny;
    this.presetTypeFocus = presetTypeFocus;
    this.presetTypeRelax = presetTypeRelax;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RandomPresetFragmentBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RandomPresetFragmentBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.random_preset_fragment, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RandomPresetFragmentBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.play_preset_button;
      FloatingActionButton playPresetButton = rootView.findViewById(id);
      if (playPresetButton == null) {
        break missingId;
      }

      id = R.id.preset_intensity;
      FlexRadioGroup presetIntensity = rootView.findViewById(id);
      if (presetIntensity == null) {
        break missingId;
      }

      id = R.id.preset_intensity__any;
      RadioButton presetIntensityAny = rootView.findViewById(id);
      if (presetIntensityAny == null) {
        break missingId;
      }

      id = R.id.preset_intensity__dense;
      RadioButton presetIntensityDense = rootView.findViewById(id);
      if (presetIntensityDense == null) {
        break missingId;
      }

      id = R.id.preset_intensity__light;
      RadioButton presetIntensityLight = rootView.findViewById(id);
      if (presetIntensityLight == null) {
        break missingId;
      }

      id = R.id.preset_random_scrollview;
      ScrollView presetRandomScrollview = rootView.findViewById(id);
      if (presetRandomScrollview == null) {
        break missingId;
      }

      id = R.id.preset_type;
      FlexRadioGroup presetType = rootView.findViewById(id);
      if (presetType == null) {
        break missingId;
      }

      id = R.id.preset_type__any;
      RadioButton presetTypeAny = rootView.findViewById(id);
      if (presetTypeAny == null) {
        break missingId;
      }

      id = R.id.preset_type__focus;
      RadioButton presetTypeFocus = rootView.findViewById(id);
      if (presetTypeFocus == null) {
        break missingId;
      }

      id = R.id.preset_type__relax;
      RadioButton presetTypeRelax = rootView.findViewById(id);
      if (presetTypeRelax == null) {
        break missingId;
      }

      return new RandomPresetFragmentBinding((ConstraintLayout) rootView, playPresetButton,
          presetIntensity, presetIntensityAny, presetIntensityDense, presetIntensityLight,
          presetRandomScrollview, presetType, presetTypeAny, presetTypeFocus, presetTypeRelax);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
