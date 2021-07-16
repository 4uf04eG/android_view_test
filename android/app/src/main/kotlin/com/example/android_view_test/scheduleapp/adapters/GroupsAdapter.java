package com.example.android_view_test.scheduleapp.adapters;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_view_test.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    private static final int ITEM_GROUP = 0;
    private static final int ITEM_YEAR_HEADER = 1;

    private static final int[] HEADER_COLORS = new int[] {
            Color.parseColor("#e32322"), //Red
            Color.parseColor("#f28e1c"), //Orange
            Color.parseColor("#454e99"), //Blue
            Color.parseColor("#008f5a"), //Dark green
            Color.parseColor("#c5037d"), //Pink
            Color.parseColor("#8dbb25")  //Bright green
    };

    private static List<String> groups;


    public GroupsAdapter() {
        groups = new ArrayList<>();
    }

    public GroupsAdapter(List<String> groups) {
        GroupsAdapter.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_GROUP) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_groups, parent, false));
        }

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_groups, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return Character.isDigit(groups.get(position).charAt(0)) ? ITEM_YEAR_HEADER : ITEM_GROUP;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (groups == null) return;

        if (getItemViewType(position) == ITEM_YEAR_HEADER) {
            int index = Character.getNumericValue(groups.get(position).charAt(0)) - 1;
            int curColor;

            if (index <= 5) {
                curColor = HEADER_COLORS[index];
            } else {
                curColor = HEADER_COLORS[new Random().nextInt(5)];
            }

            String year = holder.itemView.getResources().getString(R.string.groups_year);
            holder.parent.setText(String.format(Locale.ENGLISH, "%d %s", index + 1, year));
            holder.parent.setBackgroundColor(curColor);
            holder.arrow.getBackground().setColorFilter(curColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.parent.setText(groups.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void refreshData(List<String> newData) {
        if (newData != null) {
            groups = newData;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView parent;
        private final View arrow;

        ViewHolder(final View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            arrow = itemView.findViewById(R.id.image_arrow);
        }
    }
}
