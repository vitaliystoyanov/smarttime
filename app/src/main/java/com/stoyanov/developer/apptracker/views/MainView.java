package com.stoyanov.developer.apptracker.views;

public interface MainView {

    void showConfirmationDialog();

    void showUsedAppsList();

    void goToCharts();

    void goToSettings();

    boolean checkStateService();

    void syncStateSwitch(boolean state);

    void visibleHomeButton(boolean isVisible);

    void showSnackbar(boolean isEnable);

    void showSnackbarOnDelete();

    void enableService();

    void disableService();
}
