package com.stoyanov.developer.apptracker.presenters;

import com.stoyanov.developer.apptracker.models.ApplicationUsed;
import com.stoyanov.developer.apptracker.views.ApplicationsUsedView;

public class ApplicationsUsedPresenter extends BasePresenter<ApplicationUsed,
        ApplicationsUsedView> {

    @Override
    protected void updateView() {
        view().setTime(split(model.getSpendTime()));
        view().setApplicationName(model.getAppName());
    }

    private String split(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
