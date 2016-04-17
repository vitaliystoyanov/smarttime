package com.stoyanov.developer.apptracker.views;

import com.stoyanov.developer.apptracker.models.ApplicationUsed;

import java.util.List;

public interface ChartView {

    void displayToday(int[] data);

    void displayLastWeek(int[] data);

    void displayProgress(boolean state);

    void showStateProcessing();

    void displayEmptyState(boolean state);

    void displayTotalTimeSpent(int seconds);
}
