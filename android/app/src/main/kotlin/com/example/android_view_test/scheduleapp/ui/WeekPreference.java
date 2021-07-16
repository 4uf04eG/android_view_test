package com.example.android_view_test.scheduleapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;

import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.fragments.MoreFragment;
import com.example.android_view_test.scheduleapp.fragments.ScheduleFragment;
import com.example.android_view_test.scheduleapp.helpers.StorageHelper;

public class WeekPreference extends Preference {
    private static final String NUM_OF_WEEK = "current_week";
    private static final String WEEK_COUNT = "week_count";

    public WeekPreference(Context context) {
        super(context);
        setIcon(R.drawable.more_ic_view_week_24dp);
        setKey(NUM_OF_WEEK);
    }

    public WeekPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setIcon(R.drawable.more_ic_view_week_24dp);
        setKey(NUM_OF_WEEK);
    }

    public WeekPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setIcon(R.drawable.more_ic_view_week_24dp);
        setKey(NUM_OF_WEEK);
    }

    @Override
    public CharSequence getSummary() {
        int value = getPersistedInt(-1);
        Resources resources = getContext().getResources();

        switch(value) {
            case 0:
                return resources.getString(R.string.week_first);
            case 1:
                return resources.getString(R.string.week_second);
            case 2:
                return resources.getString(R.string.week_third);
            case 3:
                return resources.getString(R.string.week_fourth);
            default:
                return String.valueOf(value + 1);

        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("more");

        WeekDialog dialog = new WeekDialog();
        dialog.setTargetFragment(fragment, 0);
        dialog.show(activity.getSupportFragmentManager(), "week_selector");
    }

    public static class WeekDialog extends DialogFragment {
        @NonNull
        @Override
        @SuppressWarnings("ConstantConditions")
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Context context = requireContext();

            NumberPicker numberPicker = new NumberPicker(requireActivity());
            numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(StorageHelper.findIntInShared(context, WEEK_COUNT));
            numberPicker.setValue(StorageHelper.findIntInShared(context, NUM_OF_WEEK) + 1);

            return new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.more_week_selector)
                    .setPositiveButton(R.string.confirmation, (dialog, which) -> {
                        StorageHelper.addToShared(context, NUM_OF_WEEK,
                                numberPicker.getValue() - 1);

                        Preference preference =
                                ((MoreFragment) getTargetFragment()).findPreference(NUM_OF_WEEK);
                        preference.setSummary(preference.getSummary());

                        ScheduleFragment fragment = (ScheduleFragment) requireActivity()
                                .getSupportFragmentManager()
                                .findFragmentByTag("schedule");

                        if (fragment != null) {
                            fragment.addScheduleToView(StorageHelper.findScheduleInShared(context));
                        }
                    })
                    .setNegativeButton(R.string.cancellation, (dialog, which) -> {

                    })
                    .setView(numberPicker)
                    .create();
        }
    }
}
