package com.stoyanov.developer.apptracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.stoyanov.developer.apptracker.ApplicationsUsedLoader;
import com.stoyanov.developer.apptracker.R;
import com.stoyanov.developer.apptracker.adapters.ApplicationsUsedAdapter;
import com.stoyanov.developer.apptracker.adapters.StringSpinnerAdapter;
import com.stoyanov.developer.apptracker.models.ApplicationUsed;
import com.stoyanov.developer.apptracker.presenters.DataPresenter;
import com.stoyanov.developer.apptracker.views.DataView;

import java.util.List;

public class DataFragment extends Fragment implements DataView,
        LoaderManager.LoaderCallbacks<List<ApplicationUsed>>, AdapterView.OnItemSelectedListener,
        Updatable {

    public static final String EXTRA_CURRENT_ITEM_SPINNER = "current_item_spinner";
    public static final String TAG = "DataFragment";
    private static final int ID_LOADER_DATA = 1;
    public static final int POSITION_TODAY = 0;

    private boolean isFirstSelectedItemSpinner;
    private DataPresenter presenter;
    private ApplicationsUsedAdapter adapter;
    private LinearLayout layoutEmptyState;
    private CircularProgressView progressView;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private int currentPositionItemSpinner;
    private int lastItemPosition;
    private Animation animationScale;

    public DataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_used_applications, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        animationScale = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up);
        layoutEmptyState = (LinearLayout) getActivity().findViewById(R.id.linearlayout_empty_state_data);
        progressView = (CircularProgressView) getActivity().findViewById(R.id.progress_view);
        presenter = new DataPresenter();
        presenter.bindView(this);
        setupRecycleView();
        setupSpinner();
    }

    private void setupSpinner() {
        spinner = (Spinner) getActivity().findViewById(R.id.toolbar_spinner);
        StringSpinnerAdapter adapter = new StringSpinnerAdapter(getActivity());
        adapter.addItems(getResources().getStringArray(R.array.spinner_items_data));
        spinner.setAdapter(null);
        spinner.setAdapter(adapter);
    }

    private void setupRecycleView() {
        recyclerView = (RecyclerView) getActivity()
                .findViewById(R.id.recycler_view_used_apps);

        recyclerView.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));

        adapter = new ApplicationsUsedAdapter(getActivity());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: position = " + position);
        if (isFirstSelectedItemSpinner) {
            lastItemPosition = currentPositionItemSpinner;
            currentPositionItemSpinner = position;
            switchSpinner(position);
        }
        isFirstSelectedItemSpinner = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void switchSpinner(int position) {
        if (position == 3) {
            presenter.onClickAllTime();
        } else if (position == 1) {
            presenter.onClickLastWeek();
        } else if (position == 2) {
            presenter.onClickLastMonth();
        } else if (position == 4) {
            presenter.onClickHistory();
        } else if (position == POSITION_TODAY) {
            presenter.onClickToday();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: isFirstSelectedItemSpinner == " + isFirstSelectedItemSpinner);
        spinner.setOnItemSelectedListener(this);
        getLoaderManager().initLoader(ID_LOADER_DATA, null, this);
        onUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        isFirstSelectedItemSpinner = false;
        spinner.setOnItemSelectedListener(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: isFirstSelectedItemSpinner == " + isFirstSelectedItemSpinner);
        presenter.unbindView();
    }

    @Override
    public void displayData(List<ApplicationUsed> list) {
/*        for (ApplicationUsed item : list) {
            Log.d(TAG, "displayData: item load = " + item.toString());
        }*/
        adapter.clearAndAddAll(list);
        recyclerView.setAlpha(0f);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
    }

    @Override
    public void showStateProcessing() {
        Log.d(TAG, "showStateProcessing: lastItemPosition = " + lastItemPosition);
        Snackbar.make(getActivity().findViewById(R.id.container), "Data processing. Please, wait",
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showQuantityOfData(int quantity) {

    }

    @Override
    public void displayEmptyState(boolean isVisible) {
        if (isVisible) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            layoutEmptyState.startAnimation(animationScale);
        } else {
            layoutEmptyState.setVisibility(View.INVISIBLE);
        }
        layoutEmptyState.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void displayProgress(boolean isVisible) {
        progressView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public Loader<List<ApplicationUsed>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        return new ApplicationsUsedLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ApplicationUsed>> loader, List<ApplicationUsed> data) {
        Log.d(TAG, "onLoadFinished: currentPositionItemSpinner = " + currentPositionItemSpinner);
        presenter.onLoadFinished(data);
        spinner.setSelection(currentPositionItemSpinner);
        switchSpinner(currentPositionItemSpinner);
    }

    @Override
    public void onLoaderReset(Loader<List<ApplicationUsed>> loader) {
        Log.d(TAG, "onLoaderReset: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_CURRENT_ITEM_SPINNER, currentPositionItemSpinner);
        Log.d(TAG, "onSaveInstanceState: currentPositionItemSpinner = " + currentPositionItemSpinner);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPositionItemSpinner = savedInstanceState.getInt(EXTRA_CURRENT_ITEM_SPINNER, 0);
            Log.d(TAG, "onViewStateRestored: currentPositionItemSpinner = " + currentPositionItemSpinner);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onUpdate() {
        Log.d(TAG, "onUpdate: ");
        getLoaderManager().restartLoader(ID_LOADER_DATA, null, this);
    }
}