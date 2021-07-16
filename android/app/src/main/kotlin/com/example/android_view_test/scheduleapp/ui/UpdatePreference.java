package com.example.android_view_test.scheduleapp.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.helpers.BackgroundHelper;
import com.example.android_view_test.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies;
import com.example.android_view_test.scheduleapp.helpers.StorageHelper;

public class UpdatePreference extends ListPreference {
    private static final String UPDATE_FREQUENCY = "schedule_update_frequency";

    public UpdatePreference(Context context) {
        super(context);
        setDialogTitle(R.string.more_update_frequency_selector);
    }

    public UpdatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogTitle(R.string.more_update_frequency_selector);
    }

    public UpdatePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDialogTitle(R.string.more_update_frequency_selector);
    }

    @Override
    public CharSequence getSummary() {
        int storedValue = getPersistedInt(0);

        switch (UpdateFrequencies.toEnum(storedValue)) {
            case EVERY_DAY:
                return getContext().getResources().getString(R.string.update_every_day);
            case EVERY_WEEK:
                return getContext().getResources().getString(R.string.update_every_week);
            case EVERY_MONTH:
                return getContext().getResources().getString(R.string.update_every_month);
            case NEVER:
                return getContext().getResources().getString(R.string.update_never);
            default:
                return getContext().getResources().getString(R.string.preference_not_specified);
        }
    }

    @Override
    public void setDialogTitle(CharSequence dialogTitle) {
        super.setDialogTitle(
                getContext().getResources().getString(R.string.more_update_frequency_selector));
    }

    @Override
    public void setDialogTitle(int dialogTitleResId) {
        super.setDialogTitle(R.string.more_update_frequency_selector);
    }

    @Override
    protected boolean persistString(String value) {
        int parsedValue = Integer.parseInt(value);
        replaceUpdateTask(parsedValue);

        return persistInt(parsedValue);
    }

    @Override
    public int findIndexOfValue(String value) {
        return Integer.parseInt(value);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedInt(0));
    }

    @Override
    protected int getPersistedInt(int defaultReturnValue) {
        return StorageHelper.findIntInShared(getContext(), UPDATE_FREQUENCY);
    }

    private void replaceUpdateTask(int value) {
        switch (UpdateFrequencies.toEnum(value)) {
            case EVERY_DAY:
                BackgroundHelper.registerUpdateTask(getContext(), 1);
                break;
            case EVERY_WEEK:
                BackgroundHelper.registerUpdateTask(getContext(), 7);
                break;
            case EVERY_MONTH:
                BackgroundHelper.registerUpdateTask(getContext(), 30);
                break;
            case NEVER:
                BackgroundHelper.removeUpdateTask(getContext());
                break;
        }
    }
}
