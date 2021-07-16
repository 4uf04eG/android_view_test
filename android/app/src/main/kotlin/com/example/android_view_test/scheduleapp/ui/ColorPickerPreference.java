package com.example.android_view_test.scheduleapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.helpers.AppStyleHelper;

import java.lang.ref.WeakReference;

public class ColorPickerPreference extends Preference {
    private static WeakReference<ImageView> colorView;
    private static int color;

    public ColorPickerPreference(Context context) {
        super(context);
        setWidgetLayoutResource(R.layout.widget_color_picker_preference);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.widget_color_picker_preference);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.widget_color_picker_preference);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        colorView = new WeakReference<>((ImageView) holder.findViewById(R.id.color_image));

        if (color == 0) color = AppStyleHelper.getDefaultColor(getContext());

        switchViewColorOnDependencyChange(getDependencyPreference());
    }

    @Override
    protected void onClick() {
        super.onClick();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("more");

        ColorPickerFragment dialog = new ColorPickerFragment();
        dialog.setTargetFragment(fragment, 0);
        dialog.show(activity.getSupportFragmentManager(), "color_picker");
    }

    private static void changeViewColor(int color) {
        if (colorView == null) return;

        Drawable background = colorView.get().getBackground();
        background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void switchViewColorOnDependencyChange(SwitchPreference dependency) {
        if (!dependency.isChecked()) {
            changeViewColor(color);
        } else {
            changeViewColor(ColorUtils.setAlphaComponent(color, 90));
        }
    }

    private SwitchPreference getDependencyPreference() {
        return findPreferenceInHierarchy(getDependency());
    }

    public static class ColorPickerFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Context context = requireContext();
            final AppCompatActivity activity = (AppCompatActivity) context;

            return ColorPickerDialogBuilder
                    .with(context)
                    .setTitle(R.string.more_color_title)
                    .initialColor(AppStyleHelper.getDefaultColor(context))
                    .lightnessSliderOnly()
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(calculateColorPickerDensity(context))
                    .setOnColorChangedListener(selectedColor -> {
                        AppStyleHelper.setStyleDynamically
                                (activity, selectedColor, activity.getSupportActionBar());
                        changeViewColor(selectedColor);
                    })
                    .setPositiveButton(R.string.confirmation, (positiveD, selectedColor, allColors) -> {
                        AppStyleHelper.saveColorScheme
                                (activity, selectedColor);
                        color = selectedColor;
                    })
                    .setNegativeButton(R.string.cancellation, (negativeD, which) -> {
                        AppStyleHelper.setDefaultColor
                                (activity, activity.getSupportActionBar());
                        changeViewColor(AppStyleHelper.getDefaultColor(context));
                    })
                    .build();
        }

        @Override
        public void onCancel(@NonNull DialogInterface dialog) {
            super.onCancel(dialog);
            AppCompatActivity activity = (AppCompatActivity) requireActivity();

            AppStyleHelper.setDefaultColor
                    (requireActivity(), activity.getSupportActionBar());
            changeViewColor(AppStyleHelper.getDefaultColor(requireContext()));
        }

        private static int calculateColorPickerDensity(Context context) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            if (metrics.heightPixels > metrics.widthPixels) {
                return (int) ((float) metrics.heightPixels / (float) metrics.widthPixels * 6.5);
            } else {
                return (int) ((float) metrics.widthPixels / (float) metrics.heightPixels * 4.5);
            }
        }
    }
}
