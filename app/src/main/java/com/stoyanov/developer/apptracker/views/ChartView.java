package com.stoyanov.developer.apptracker.views;

public interface ChartView {

    void displayToday(int[] data);

    void displayLastWeek(int[] data);

    void displayAllTime(int[] data);

    void displayProgress(boolean state);

    void showStateProcessing();

    void displayEmptyState(boolean state);

    void displayTotalTimeSpent(int seconds);
}
