package com.example.android_view_test.scheduleapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.containers.ScheduleContainer;
import com.example.android_view_test.scheduleapp.fragments.CallScheduleFragment;
import com.example.android_view_test.scheduleapp.fragments.MoreFragment;
import com.example.android_view_test.scheduleapp.fragments.ScheduleFragment;
import com.example.android_view_test.scheduleapp.helpers.AppStyleHelper;
import com.example.android_view_test.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies;
import com.example.android_view_test.scheduleapp.helpers.StorageHelper;
import com.example.android_view_test.scheduleapp.parsers.GroupNameParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.flutter.embedding.android.FlutterFragment;

public class MainNativeActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String GROUP_NAME = "group_name";
    private static final String SCHEDULE_LINK = "schedule_link";
    private static final String NUM_OF_WEEK = "current_week";
    private static final String UPDATE_FREQUENCY = "schedule_update_frequency";
    private static final String AUTO_WEEK_CHANGE = "auto_week_change";

    private static long bottomNavLastClickTime;
    private static boolean isGroupSelectorOpened;

    private TextView toolbarSubtitle;
    private BottomNavigationView bottomNavigation;
    private RadioGroup radioGroup;

    // Define a tag String to represent the FlutterFragment within this
    // Activity's FragmentManager. This value can be whatever you'd like.
    private static final String TAG_FLUTTER_FRAGMENT = "flutter_fragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleUrlIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        AppStyleHelper.restoreMainStyle(this, toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarSubtitle = findViewById(R.id.toolbar_subtitle);
        radioGroup = findViewById(R.id.toolbar_weeks_group);

        bottomNavigation = findViewById(R.id.bottom_nav_view);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        if (StorageHelper.findStringInShared(this, SCHEDULE_LINK) == null) {
            setInitialParameters();
            startActivityForResult(new Intent(this, AllGroupsActivity.class), 0);
            isGroupSelectorOpened = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            isGroupSelectorOpened = false;
        }

        if (requestCode == 1) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            Fragment fragment = fm.findFragmentByTag("schedule");
            MoreFragment settings = (MoreFragment) fm.findFragmentByTag("more");
            Preference preference = settings.findPreference("group_name");

            settings.findPreference("group_name").setSummary(preference.getSummary());

            if (fragment != null) {
                transaction.remove(fragment).commit();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (StorageHelper.findBooleanInShared(this, AUTO_WEEK_CHANGE)) {
            setRightWeek();
        }

        if (!isGroupSelectorOpened) {
            setFragmentBySelectedItemId(bottomNavigation.getSelectedItemId());
        }
     }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("schedule");

        if (fragment != null && fragment.isVisible()) {
            super.onBackPressed();
        } else {
            bottomNavigation.setSelectedItemId(R.id.navigation_schedule);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        long curTime = SystemClock.elapsedRealtime();

        if (curTime - bottomNavLastClickTime < 100) return false;

        bottomNavLastClickTime = curTime;

        return setFragmentBySelectedItemId(menuItem.getItemId());
    }

    public void onWeekChangeButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_weeks_first:
                StorageHelper.addToShared(this, NUM_OF_WEEK, 0);
                break;
            case R.id.toolbar_weeks_second:
                StorageHelper.addToShared(this, NUM_OF_WEEK, 1);
                break;
        }

        ScheduleFragment schedule =
                (ScheduleFragment) getSupportFragmentManager().findFragmentByTag("schedule");
        MoreFragment more =
                (MoreFragment) getSupportFragmentManager().findFragmentByTag("more");

        if (schedule != null) {
            ScheduleContainer container = StorageHelper.findScheduleInShared(this);

            if (container != null) schedule.addScheduleToView(container);
        }

        if (more != null) {
            Preference preference = more.findPreference("current_week");
            preference.setSummary(preference.getSummary());
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void changeToolbarLayout(boolean visible) {
        if (!visible) {
            toolbarSubtitle.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("E MMM d");
        int weekNum = StorageHelper.findIntInShared(this, NUM_OF_WEEK);

        String subtitle = getResources().getString(R.string.schedule_toolbar_subtitle,
                format.format(calendar.getTime()));
        toolbarSubtitle.setVisibility(View.VISIBLE);
        toolbarSubtitle.setText(subtitle);

        radioGroup.setVisibility(View.VISIBLE);

        switch (weekNum) {
            case 0:
                radioGroup.check(R.id.toolbar_weeks_first);
                break;
            case 1:
                radioGroup.check(R.id.toolbar_weeks_second);
                break;
        }
    }

    private void handleUrlIntent() {
        Uri appLinkData = getIntent().getData();
        String regex = "https://old.ulstu.ru/schedule/students/part\\d+/\\d+.htm";

        if (appLinkData != null && appLinkData.toString().matches(regex)) {
            StorageHelper.addToShared(this, SCHEDULE_LINK, appLinkData.toString());
            StorageHelper.clearSchedule(this);
            tryLoadGroupName();
        }
    }

    private void tryLoadGroupName() {
        new Thread(() -> {
            try {
                StorageHelper.addToShared(this,
                        GROUP_NAME, new GroupNameParser().execute(this).get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //For now it works only with two weeks
    private void setRightWeek() {
        int firstWeekOfFirstSemester = 36;
        int firstWeekOfSecondSemester = 6;
        int monthOfSeptember = 9;
        int weekDiff;

        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.add(Calendar.DATE, 1);

        if (calendar.get(Calendar.MONTH) >= monthOfSeptember) {
            weekDiff = calendar.get(Calendar.WEEK_OF_YEAR) - firstWeekOfFirstSemester;
        } else if (calendar.get(Calendar.WEEK_OF_YEAR) >= 6){
            weekDiff = calendar.get(Calendar.WEEK_OF_YEAR) - firstWeekOfSecondSemester;
        } else {
            weekDiff = calendar.get(Calendar.WEEK_OF_YEAR);
        }

        if (StorageHelper.findIntInShared(this, NUM_OF_WEEK) != weekDiff % 2) {
            StorageHelper.addToShared(this, NUM_OF_WEEK, weekDiff % 2);
            ScheduleFragment fragment =
                    (ScheduleFragment) getSupportFragmentManager().findFragmentByTag("schedule");

            if (fragment != null) {
                fragment.addScheduleToView(StorageHelper.findScheduleInShared(this));
            }
        }
    }

    private void setInitialParameters() {
        int defaultUpdateFrequency = UpdateFrequencies.EVERY_MONTH.ordinal();

        StorageHelper.addToShared(this, NUM_OF_WEEK, 0);
        StorageHelper.addToShared(this, UPDATE_FREQUENCY, defaultUpdateFrequency);
        StorageHelper.addToShared(this, AUTO_WEEK_CHANGE, true);

        AppStyleHelper.initializeStyle(this);
    }

    private boolean setFragmentBySelectedItemId(int id) {
        switch (id) {
            case R.id.navigation_schedule:
                loadFragment(new ScheduleFragment());
                return true;
            case R.id.navigation_call_schedule:
                loadFragment(new CallScheduleFragment());
                return true;
            case R.id.navigation_more:
                loadFragment(new MoreFragment());
                return true;
            default:
                return false;
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment instanceof ScheduleFragment) {
            addFragment(fragment, "schedule");
            AppStyleHelper.restoreTabLayoutStyle(this);
        } else if (fragment instanceof MoreFragment) {
            addFragment(fragment, "more");
        } else {
            addFragment(fragment, "call_schedule");
        }
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment cachedFragment = fragmentManager.findFragmentByTag(tag);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (Fragment addedFragment : fragmentManager.getFragments()) {
            transaction.hide(addedFragment);
        }

        if (cachedFragment != null) {
            transaction.show(cachedFragment).commit();

            if (cachedFragment instanceof ScheduleFragment) {
                if (((ScheduleFragment) cachedFragment).getLoadStatus()) {
                    changeToolbarLayout(true);
                }
            } else {
                changeToolbarLayout(false);
            }
        } else {
            transaction.add(R.id.main_container, fragment, tag).commit();
        }
    }
}
