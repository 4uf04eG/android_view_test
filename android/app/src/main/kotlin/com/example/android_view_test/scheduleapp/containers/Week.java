package com.example.android_view_test.scheduleapp.containers;

import java.util.ArrayList;

public class Week {
    private final ArrayList<ArrayList<ClassData>> data;
    private int currentDay;

    public Week() {
        data = new ArrayList<>();
        currentDay = 0;
    }

    void add(String value) {
        while (currentDay >= size())
            data.add(new ArrayList<>());

        data.get(currentDay).add(new ClassData(value));
    }

    void switchToNextDay() {
        while (currentDay >= size())
            data.add(new ArrayList<>());

        currentDay++;
    }

    public ArrayList<ClassData> get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }
}
