package com.stoyanov.developer.apptracker.views;

import com.stoyanov.developer.apptracker.models.ApplicationUsed;

import java.util.List;

public interface DataView {

    void showTotalSpentTime(int seconds);

    void displayEmptyState(boolean isVisible);

    void displayProgress(boolean isVisible);

    void displayData(List<ApplicationUsed> list);

    void showStateProcessing();

}
