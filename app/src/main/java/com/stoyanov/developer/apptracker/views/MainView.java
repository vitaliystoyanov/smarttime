package com.stoyanov.developer.apptracker.views;

public interface MainView {

    void showConfirmationDialog();

    void goToCharts();

    void showListOfData(boolean animationExit);

    void goToSettings();

    boolean checkStateService();

    void syncStateSwitch(boolean state);

    void visibleHomeButton(boolean isVisible);

    void showStateService(boolean isEnable);

    void showMassageAfterDelete();

    void enableService();

    void disableService();
}
