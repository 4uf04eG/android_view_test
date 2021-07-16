package com.example.android_view_test.scheduleapp.containers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScheduleContainer implements Serializable {
    private final List<Week> schedule;
    private int currentWeek;

    public ScheduleContainer() {
        schedule = new ArrayList<>();
        currentWeek = 0;
    }

    public void add(String data) {
        while (currentWeek >= size())
            schedule.add(new Week());

        schedule.get(currentWeek).add(data);
    }

    public void switchToNextDay() {
        while (currentWeek >= size())
            schedule.add(new Week());

        schedule.get(currentWeek).switchToNextDay();
    }

    public Week get(int index) {
        return schedule.get(index);
    }

    public void switchToNextWeek() {
        currentWeek++;
    }

    public int size() {
        return schedule.size();
    }
}