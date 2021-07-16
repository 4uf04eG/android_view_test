package com.example.android_view_test.scheduleapp.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.activities.AllGroupsActivity;
import com.example.android_view_test.scheduleapp.activities.MainNativeActivity;

public class AppStyleHelper {
    private static final String COLOR_SCHEME = "default_color";
    private static final String DARK_THEME_TYPE = "dark_theme";

    public static void initializeStyle(@NonNull Context context) {
        int color = context.getResources().getColor(R.color.colorPrimary);

        StorageHelper.addToShared(context, COLOR_SCHEME, color);
        StorageHelper.addToShared(context, DARK_THEME_TYPE, false);
    }

    public static void restoreMainStyle(@NonNull MainNativeActivity activity, Toolbar toolbar) {
        int color = getDefaultColor(activity);
        boolean darkThemeType = StorageHelper.findBooleanInShared(activity, DARK_THEME_TYPE);
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_nav_view);

        if (darkThemeType) {
            color = activity.getResources().getColor(R.color.colorPrimaryDarkTheme);
        }

        setStyle(color, toolbar, bottomNav);
    }

    public static void restoreAllGroupsStyle(@NonNull AllGroupsActivity activity, ActionBar actionBar) {
        int color = getDefaultColor(activity);
        int progressBarColor = color;
        boolean darkThemeType = StorageHelper.findBooleanInShared(activity, DARK_THEME_TYPE);


        if (darkThemeType) {
            color = activity.getResources().getColor(R.color.colorPrimaryDarkTheme);
            progressBarColor = activity.getResources().getColor(R.color.colorAccent);
        }

        setBackground(activity, actionBar, color);

        ProgressBar progressBar = activity.findViewById(R.id.all_progress_bar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(progressBarColor, PorterDuff.Mode.MULTIPLY);
    }

    public static void restoreTabLayoutStyle(@NonNull Activity activity) {
        boolean darkThemeType = StorageHelper.findBooleanInShared(activity, DARK_THEME_TYPE);
        TabLayout tabLayout = activity.findViewById(R.id.tabs);
        int color = getDefaultColor(activity);
        int progressBarColor = color;

        if (tabLayout == null) return;

        if (darkThemeType) {
            color = activity.getResources().getColor(R.color.colorPrimaryDarkTheme);
            progressBarColor = activity.getResources().getColor(R.color.colorAccent);
        }

        tabLayout.setBackgroundColor(color);

        ProgressBar progressBar = activity.findViewById(R.id.all_progress_bar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(progressBarColor, PorterDuff.Mode.MULTIPLY);
    }

    public static int getDefaultColor(@NonNull Context context) {
        int color = StorageHelper.findIntInShared(context, COLOR_SCHEME);

        if (color != Integer.MIN_VALUE) {
            return color;
        }

        return context.getResources().getColor(R.color.colorPrimary);
    }

    public static void setDefaultColor(@NonNull Activity activity, ActionBar actionBar) {
        int color = StorageHelper.findIntInShared(activity, COLOR_SCHEME);

        AppStyleHelper.setStyleDynamically(activity, color, actionBar);
    }

    public static void setStyleDynamically(@NonNull Activity activity, int color, ActionBar actionBar) {
        if (actionBar == null) {
            return;
        }

        actionBar.setBackgroundDrawable(new ColorDrawable(color));
        activity.findViewById(R.id.bottom_nav_view).setBackground(new ColorDrawable(color));
    }

    public static void saveColorScheme(@NonNull Activity activity, int color) {
        StorageHelper.addToShared(activity, COLOR_SCHEME, color);
    }

    private static void setBackground(@NonNull Activity activity, ActionBar actionBar, int color) {
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(color));
            setStatusBarColor(activity, color);
        }
    }

    private static void setStyle(int color, Toolbar toolbar, BottomNavigationView bottomNav) {
        if (toolbar == null) {
            return;
        }

        toolbar.setBackgroundColor(color);
        bottomNav.setBackground(new ColorDrawable(color));

    }

    private static void setStatusBarColor(@NonNull Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}
