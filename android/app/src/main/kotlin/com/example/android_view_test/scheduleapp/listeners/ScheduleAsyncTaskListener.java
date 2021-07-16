package com.example.android_view_test.scheduleapp.listeners;

import android.content.Context;

import com.example.android_view_test.scheduleapp.containers.ScheduleContainer;

public interface ScheduleAsyncTaskListener {
    void addScheduleToView(ScheduleContainer scheduleContainer);

    void storeSchedule(Context context, ScheduleContainer scheduleContainer);

    void showErrorToast(Context context);

    void finishRefreshing();

    void reloadSchedule();
}
