package com.example.android_view_test.scheduleapp.containers;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupsContainer implements Parcelable {
    public static final Creator<GroupsContainer> CREATOR = new Creator<GroupsContainer>() {
        @Override
        public GroupsContainer createFromParcel(Parcel in) {
            return new GroupsContainer(in);
        }

        @Override
        public GroupsContainer[] newArray(int size) {
            return new GroupsContainer[size];
        }
    };

    private final List<List<Pair>> data;
    private int currentColumn;

    public GroupsContainer() {
        data = new ArrayList<>();
        currentColumn = 0;
    }

    private GroupsContainer(Parcel in) {
        data = new ArrayList<>();
        currentColumn = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(currentColumn);
    }

    public void add(String name, String value) {
        while (currentColumn >= size())
            data.add(new ArrayList<>());

        if (!name.isEmpty()) data.get(currentColumn).add(new Pair(name, value));

        currentColumn++;
    }

    public String findLink(String name) {
        for (List<Pair> row : data)
            for (Pair item : row)
                if (item.name.equals(name)) {
                    return item.link;
                }

        return null;
    }

    /**
     * If data is empty returns empty list. Sorts array using custom comparator.
     * After that adds every name from Pair in data to ArrayList.
     * But there's limitation based on fact that to ignore header duplicates it skips first column.
     * And if there's not two links might have some problems.
     *
     * @return sorted array list of strings
     */
    public List<String> toSortedLinearList() {
        if (size() == 0) return new ArrayList<>();

        for (List<Pair> row : data)
            Collections.sort(row, new PairListComparator());

        List<String> linearGroups = new ArrayList<>();

        for (int year = 0; year < data.size(); year++) {
            linearGroups.add(Integer.toString(year + 1));

            for (int i = 0; i < data.get(year).size(); i++) {
                linearGroups.add(data.get(year).get(i).name);
            }
        }

        return linearGroups;
    }

    public List<String> getAllLinks() {
        if (size() == 0) return new ArrayList<>();

        List<String> linearGroups = new ArrayList<>();

        for (List<Pair> year : data)
            for (int i = 0; i < year.size(); i++)
                linearGroups.add(year.get(i).link);

        return linearGroups;
    }

    public void switchToNextRow() { currentColumn = 0; }

    public void switchToNextColumn() { currentColumn++; }

    public int size() { return data.size(); }
}

class Pair {
    final String name;
    final String link;

    Pair(String key, String value) {
        name = key;
        link = value;
    }
}

class PairListComparator implements Comparator<Pair> {
    @Override
    public int compare(Pair o1, Pair o2) {
        return o1.name.compareTo(o2.name);
    }
}
