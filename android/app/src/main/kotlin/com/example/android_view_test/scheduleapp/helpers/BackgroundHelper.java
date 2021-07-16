package com.example.android_view_test.scheduleapp.helpers;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.android_view_test.scheduleapp.workers.RefreshWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BackgroundHelper {
    private static final String UPDATE_WORK_NAME = "ScheduleUpdate";

    public static void registerUpdateTask(Context context, int repeatIntervalDays) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                .Builder(RefreshWorker.class, repeatIntervalDays, TimeUnit.DAYS)
                .setInitialDelay(repeatIntervalDays, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UPDATE_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, workRequest);
    }

    public static boolean isTaskRegistered(Context context) {
        List<WorkInfo> workInfos = new ArrayList<>();
        try {
            workInfos = WorkManager.getInstance(context).getWorkInfosByTag(UPDATE_WORK_NAME).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       return !workInfos.isEmpty();
    }


    public static void removeUpdateTask(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(UPDATE_WORK_NAME);
    }

    public enum UpdateFrequencies {
        EVERY_DAY,
        EVERY_WEEK,
        EVERY_MONTH,
        NEVER,
        NOT_FOUND;

        public static UpdateFrequencies toEnum(int index) {
            if (index >= 0 && index < UpdateFrequencies.values().length) {
                return UpdateFrequencies.values()[index];
            } else {
                return NOT_FOUND;
            }
        }
    }
}
