package com.example.android_view_test.scheduleapp;

import android.app.Application;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.android_view_test.scheduleapp.helpers.BackgroundHelper;
import com.example.android_view_test.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies;
import com.example.android_view_test.scheduleapp.helpers.StorageHelper;
import com.example.android_view_test.scheduleapp.utils.TLSSocketFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;

import static com.example.android_view_test.scheduleapp.helpers.BackgroundHelper.UpdateFrequencies.NEVER;

public class UserPreferences extends Application {
    private static final String DARK_THEME_TYPE = "dark_theme";
    private static final String UPDATE_FREQUENCY = "schedule_update_frequency";

    public FlutterEngine flutterEngine;

    @Override
    public void onCreate() {
        setDarkTheme();
        setUpdateTask();
        cacheFlutter();
        super.onCreate();
    }

    private void setDarkTheme() {
        boolean darkTheme = StorageHelper.findBooleanInShared(this, DARK_THEME_TYPE);

        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setUpdateTask() {
        int updateStatus = StorageHelper.findIntInShared(this, UPDATE_FREQUENCY);
        boolean isUpdateDisabled = UpdateFrequencies.toEnum(updateStatus) == NEVER;

        if (!BackgroundHelper.isTaskRegistered(this) && !isUpdateDisabled) {
            BackgroundHelper.registerUpdateTask(this, 7);
        }
    }

    private void cacheFlutter() {
        super.onCreate();
        // Instantiate a FlutterEngine.
        flutterEngine = new FlutterEngine(this);

        // Start executing Dart code to pre-warm the FlutterEngine.
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );

        // Cache the FlutterEngine to be used by FlutterActivity.
        FlutterEngineCache
                .getInstance()
                .put("my_engine_id", flutterEngine);
    }
}
