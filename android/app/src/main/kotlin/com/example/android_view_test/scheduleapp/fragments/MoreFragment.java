package com.example.android_view_test.scheduleapp.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.activities.MainNativeActivity;
import com.example.android_view_test.scheduleapp.helpers.StorageHelper;

import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;


public class MoreFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String DARK_THEME_TYPE = "dark_theme";
    private static final String SCHEDULE_LINK = "schedule_link";

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        findPreference("open_online").setOnPreferenceClickListener(this);
        findPreference("dark_theme").setOnPreferenceChangeListener(this);
        findPreference("group_name").setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "dark_theme":
                StorageHelper.addToShared(requireContext(), DARK_THEME_TYPE, newValue);

                if ((boolean) newValue) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                break;
            case "group_name":
                preference.getSummary();
                break;
        }

        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if ("open_online".equals(preference.getKey())) {
            openLinkInBrowser();
            return true;
        }

        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainNativeActivity) requireActivity()).changeToolbarLayout(false);
    }

    private void openLinkInBrowser() {
        String link = StorageHelper.findStringInShared(requireContext(), SCHEDULE_LINK);

        if (link != null) {
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "http://" + link;
            }

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        }
    }

}
