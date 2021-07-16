package com.example.android_view_test.scheduleapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.adapters.GroupsAdapter;
import com.example.android_view_test.scheduleapp.containers.GroupsContainer;
import com.example.android_view_test.scheduleapp.helpers.AppStyleHelper;
import com.example.android_view_test.scheduleapp.helpers.StorageHelper;
import com.example.android_view_test.scheduleapp.listeners.GroupsAsyncTaskListener;
import com.example.android_view_test.scheduleapp.parsers.GroupsParser;

import java.util.concurrent.ExecutionException;

public class AllGroupsActivity extends AppCompatActivity implements GroupsAsyncTaskListener {
    private static final String GROUP_NAME = "group_name";
    private static final String SCHEDULE_LINK = "schedule_link";
    private static final String ARG_SAVED_GROUPS = "saved_groups";
    private static final String ARG_RETRY_BUTTON_STATUS = "retry_button_status";

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView groupsView;
    private GroupsContainer foundGroups;
    private Boolean isGroupChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_groups);
        setTitle(R.string.group_selection);
        AppStyleHelper.restoreAllGroupsStyle(this, getSupportActionBar());

        groupsView = findViewById(R.id.groups);
        groupsView.setLayoutManager(new LinearLayoutManager(this));
        groupsView.setAdapter(new GroupsAdapter());

        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(this::tryLoadAllGroups);

        isGroupChange = getIntent().getBooleanExtra("change_group", false);

        findViewById(R.id.retry_refresh_button).setOnClickListener(v -> onRefreshButtonClick());

        if (!isGroupChange && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        if (savedInstanceState != null) {
            GroupsContainer savedGroups = savedInstanceState.getParcelable(ARG_SAVED_GROUPS);
            foundGroups = savedGroups;

            if (foundGroups == null || foundGroups.size() == 0) {
                if (savedInstanceState.getBoolean(ARG_RETRY_BUTTON_STATUS)) {
                    findViewById(R.id.retry_refresh_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.all_progress_bar).setVisibility(View.GONE);
                } else {
                    tryLoadAllGroups();
                }
            } else {
                addGroupsToView(savedGroups);
                finishRefreshing();
            }
        } else {
            tryLoadAllGroups();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean isVisible = findViewById(R.id.retry_refresh_button).getVisibility() == View.VISIBLE;

        outState.putParcelable(ARG_SAVED_GROUPS, foundGroups);
        outState.putBoolean(ARG_RETRY_BUTTON_STATUS, isVisible);
    }

    @Override
    public void onBackPressed() {
        if (!isGroupChange) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showErrorToast() {
        Toast.makeText(this,
                getString(R.string.connection_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showRetryMessage() {
        if (groupsView.getChildCount() == 0) {
            refreshLayout.setEnabled(false);
            findViewById(R.id.retry_refresh_button).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finishRefreshing() {
        refreshLayout.setEnabled(true);
        refreshLayout.setRefreshing(false);
        findViewById(R.id.all_progress_bar).setVisibility(View.GONE);
        findViewById(R.id.retry_refresh_button).setVisibility(View.GONE);
    }

    @Override
    public void addGroupsToView(GroupsContainer groups) {
        GroupsAdapter adapter = (GroupsAdapter) groupsView.getAdapter();

        if (adapter != null) {
            adapter.refreshData(groups.toSortedLinearList());
        }
    }

    public void writeGroupToSharedStorage(View view) {
        String name = ((TextView) view).getText().toString();

        if (isGroupChange) StorageHelper.clearSchedule(this);

        StorageHelper.addToShared(this, GROUP_NAME, name);
        StorageHelper.addToShared(this, SCHEDULE_LINK, foundGroups.findLink(name));

        if (StorageHelper.findStringInShared(this, SCHEDULE_LINK) == null) {
            showAlertDialog();
        } else finish();
    }

    private void onRefreshButtonClick() {
        findViewById(R.id.all_progress_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.retry_refresh_button).setVisibility(View.GONE);
        tryLoadAllGroups();
    }

    private void tryLoadAllGroups() {
        final GroupsParser sg = new GroupsParser(this);

        new Thread(() -> {
            try {
                foundGroups = sg.execute().get();
            } catch (InterruptedException e) {
                Log.d("HandledInterruptedE", e.getMessage());
            } catch (ExecutionException e) {
                Log.d("HandledExecutionE", e.getMessage());
            }
        }).start();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setTitle("Internal error");
        builder.setMessage("Please select another group");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
