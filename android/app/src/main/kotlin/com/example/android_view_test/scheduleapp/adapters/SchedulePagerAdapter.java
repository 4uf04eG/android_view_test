package com.example.android_view_test.scheduleapp.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.containers.Week;
import com.example.android_view_test.scheduleapp.fragments.SchedulePlaceholderFragment;

import java.util.ArrayList;

public class SchedulePagerAdapter extends FragmentStatePagerAdapter {
    private static Week schedule;
    private final FragmentManager fragmentManager;
    private final String[] TAB_TITLES;

    public SchedulePagerAdapter(@NonNull Context context, @NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        TAB_TITLES = context.getResources().getStringArray(R.array.days_of_week);
        schedule = new Week();
        fragmentManager = fm;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position < schedule.size()) {
            return SchedulePlaceholderFragment.newInstance(schedule.get(position), position);
        }

        return SchedulePlaceholderFragment.newInstance(new ArrayList<>(), position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    public void refreshData(@Nullable Week newData) {
        if (newData != null) {
            schedule = newData;

            for (Fragment fragment : fragmentManager.getFragments()) {
                Bundle args = fragment.getArguments();

                if (args == null) return;

                int position = args.getInt("position");
                ((SchedulePlaceholderFragment) fragment).refreshFragment(newData.get(position));
            }
        }
    }
}
