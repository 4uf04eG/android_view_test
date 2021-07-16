package com.example.android_view_test.scheduleapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.adapters.ScheduleListAdapter;
import com.example.android_view_test.scheduleapp.containers.ClassData;

import java.util.ArrayList;
import java.util.List;

public class SchedulePlaceholderFragment extends Fragment {
    private static final String ARG_SCHEDULE = "schedule";
    private static final String ARG_POS = "position";

    public static SchedulePlaceholderFragment newInstance(ArrayList<ClassData> daySchedule, int position) {
        SchedulePlaceholderFragment fragment = new SchedulePlaceholderFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ARG_SCHEDULE, daySchedule);
        args.putInt(ARG_POS, position);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<ClassData> schedule = new ArrayList<>();

        if (getArguments() != null) {
            schedule = getArguments().getParcelableArrayList(ARG_SCHEDULE);
        }

        RecyclerView recyclerView = view.findViewById(R.id.placeholder);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ScheduleListAdapter(schedule));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                boolean isScrolled = recyclerView.computeVerticalScrollOffset() != 0;
                SwipeRefreshLayout layout = requireActivity().findViewById(R.id.swipe_refresh);

                if (!isScrolled) layout.setEnabled(true);
                else layout.setEnabled(false);
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_placeholder, container, false);
    }

    public void refreshFragment(ArrayList<ClassData> newData) {
        if (!isAdded() || getView() == null) return;

        RecyclerView view = requireView().findViewById(R.id.placeholder);
        ScheduleListAdapter adapter = (ScheduleListAdapter) view.getAdapter();

        if (adapter != null) {
            adapter.refreshData(newData);
        }
    }
}
