package com.example.android_view_test.scheduleapp.listeners;

import com.example.android_view_test.scheduleapp.containers.GroupsContainer;

public interface GroupsAsyncTaskListener {
    void addGroupsToView(GroupsContainer result);

    void showErrorToast();

    void finishRefreshing();

    void showRetryMessage();
}
