package com.stoyanov.developer.apptracker.presenters;

import com.stoyanov.developer.apptracker.database.dao.DAOInterface;
import com.stoyanov.developer.apptracker.database.dao.entity.Application;
import com.stoyanov.developer.apptracker.views.MainView;

public class MainPresenter extends BasePresenter<Object, MainView> {

    public MainPresenter() {
        setModel(new Object());
    }

    @Override
    protected void updateView() {
        if (view().checkStateService()) {
            view().syncStateSwitch(true);
        }
    }

    @Override
    protected void resetState() {
    }

    public void onShowHomeButton() {
        view().visibleHomeButton(true);
    }

    public void onEnableService() {
        view().enableService();
        view().showSnackbar(true);
    }

    public void onDisableService() {
        view().disableService();
        view().showSnackbar(false);
    }

    public void onClickSettings() {
        view().goToSettings();
    }

    public void onClickCharts() {
        view().goToCharts();
    }

    public void onShowListOfApps() {
        view().visibleHomeButton(false);
        view().showUsedAppsList();
    }

    public void onClickClearData() {
        view().showConfirmationDialog();
    }

    public void onClickAgreeConfirmDialog(DAOInterface<Application> instanceDAO) {
        instanceDAO.deleteAll();
        view().showSnackbarOnDelete();
    }
}
