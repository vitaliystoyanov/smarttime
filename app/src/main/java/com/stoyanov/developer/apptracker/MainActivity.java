package com.stoyanov.developer.apptracker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stoyanov.developer.apptracker.adapters.StringSpinnerAdapter;
import com.stoyanov.developer.apptracker.database.dao.ApplicationDAO;
import com.stoyanov.developer.apptracker.fragments.ChartsFragment;
import com.stoyanov.developer.apptracker.fragments.DataFragment;
import com.stoyanov.developer.apptracker.fragments.Updatable;
import com.stoyanov.developer.apptracker.presenters.MainPresenter;
import com.stoyanov.developer.apptracker.service.TrackerService;
import com.stoyanov.developer.apptracker.views.MainView;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_LAST_FRAGMENT = "EXTRA_LAST_FRAGMENT";

    private MainPresenter mainPresenter;
    private ChartsFragment chartsFragment;
    private DataFragment dataFragment;
    private MaterialDialog confirmDialog;
    private boolean isServiceRunning;
    private String lastFragment;
    private View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            lastFragment = DataFragment.TAG;
        }
        container = findViewById(R.id.container);
        mainPresenter = new MainPresenter();
        chartsFragment = new ChartsFragment();
        dataFragment = new DataFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        setupSpinner(toolbar);
        setupDialog();
    }

    private void setupDialog() {
        confirmDialog = new MaterialDialog.Builder(this)
                .title("Delete all data?")
                .content("Delete all data")
                .positiveText("Agree")
                .negativeText("Disagree")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ApplicationDAO dao = new ApplicationDAO(getApplicationContext());
                        try {
                            //mainPresenter.onClickAgreeConfirmDialog(dao);
                        } finally {
                            dao.close();
                        }
                        Updatable interfaceUpdate = (Updatable) getSupportFragmentManager()
                                .findFragmentByTag(lastFragment);
                        if (interfaceUpdate != null) {
                            interfaceUpdate.onUpdate();
                        }
                    }
                })
                .build();
    }

    private void setupSpinner(Toolbar toolbar) {
        View toolbarSpinner = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, toolbar, false);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(toolbarSpinner, layoutParams);

        StringSpinnerAdapter adapter = new StringSpinnerAdapter(this);
        adapter.addItems(getResources().getStringArray(R.array.spinner_items_data));

        Spinner spinner = (Spinner) toolbarSpinner.findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainPresenter.unbindView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.bindView(this);
        if (lastFragment.equals(DataFragment.TAG)) {
            mainPresenter.onShowListOfData();
        } else if (lastFragment.equals(ChartsFragment.TAG)) {
            mainPresenter.onClickCharts();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        RelativeLayout layoutItem = (RelativeLayout) menu.findItem(R.id.item_menu_switch).getActionView();
        SwitchCompat serviceSwitch = (SwitchCompat) layoutItem.findViewById(R.id.switch_for_action_bar);
        serviceSwitch.setChecked(isServiceRunning);
        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mainPresenter.onEnableService();
                } else {
                    mainPresenter.onDisableService();
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
/*
            case R.id.item_settings: {
                mainPresenter.onClickSettings();
                break;
            }
*/
            case R.id.item_charts: {
                mainPresenter.onClickCharts();
                break;
            }
            case android.R.id.home: {
                mainPresenter.onBackToListOfData();
                break;
            }
            case R.id.item_clear_all_apps: {
                mainPresenter.onClickClearData();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showConfirmationDialog() {
        confirmDialog.show();
    }

    @Override
    public void showListOfData(boolean animationExit) {
        Log.d(TAG, "showListOfData: animationExit == " + animationExit);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (!animationExit) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        }
        transaction.replace(R.id.container, dataFragment, DataFragment.TAG).commit();
        lastFragment = DataFragment.TAG;
    }

    @Override
    public void goToSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void goToCharts() {
        mainPresenter.onShowHomeButton();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.container, chartsFragment, ChartsFragment.TAG)
                .commit();
        lastFragment = ChartsFragment.TAG;
    }

    @Override
    public boolean checkStateService() {
        return isServiceRunning(TrackerService.class);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void syncStateSwitch(boolean state) {
        isServiceRunning = state;
    }

    @Override
    public void visibleHomeButton(boolean isVisible) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(isVisible);
        }
    }

    @Override
    public void showMassageAfterDelete() {
        Snackbar.make(container, "All data is deleted", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showStateService(boolean isEnableService) {
        if (isEnableService) {
            Snackbar.make(container, "Start monitor", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(container, "Stop monitor", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void enableService() {
        startService(createIntent());
    }

    @Override
    public void disableService() {
        stopService(createIntent());
    }

    private Intent createIntent() {
        return new Intent(this, TrackerService.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_LAST_FRAGMENT, lastFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            lastFragment = savedInstanceState.getString(EXTRA_LAST_FRAGMENT);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
