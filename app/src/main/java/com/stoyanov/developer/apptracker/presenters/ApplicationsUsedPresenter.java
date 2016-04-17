package com.stoyanov.developer.apptracker.presenters;

import com.stoyanov.developer.apptracker.TimeConverter;
import com.stoyanov.developer.apptracker.models.ApplicationUsed;
import com.stoyanov.developer.apptracker.views.ApplicationsUsedView;

public class ApplicationsUsedPresenter extends BasePresenter<ApplicationUsed,
        ApplicationsUsedView> {

    @Override
    protected void updateView() {
        view().setTime(TimeConverter.convert(model.getTimeSpent()));
        view().setApplicationName(model.getApplicationName());
    }

}
