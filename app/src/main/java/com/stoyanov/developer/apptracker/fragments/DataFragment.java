package com.stoyanov.developer.apptracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.stoyanov.developer.apptracker.ApplicationsUsedLoader;
import com.stoyanov.developer.apptracker.R;
import com.stoyanov.developer.apptracker.adapters.ApplicationsUsedAdapter;
import com.stoyanov.developer.apptracker.models.ApplicationUsed;
import com.stoyanov.developer.apptracker.presenters.DataPresenter;
import com.stoyanov.developer.apptracker.views.DataView;

import java.util.List;

public class DataFragment extends Fragment implements DataView,
        LoaderManager.LoaderCallbacks<List<ApplicationUsed>>, AdapterView.OnItemSelectedListener {

    private static final String TAG = "DataFragment";

    private static final int ID_LOADER = 1;
    public static final int POSITION_TODAY = 0;

    private DataPresenter presenter;
    private ApplicationsUsedAdapter adapter;
    private LinearLayout layoutEmptyState;
    private CircularProgressView progressView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner spinner;
    private int currentPositionItemSpinner;
    private int lastItemPosition;

    public DataFragment() {
        presenter = new DataPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_used_applications, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        progressView = (CircularProgressView) getActivity().findViewById(R.id.progress_view);
        layoutEmptyState = (LinearLayout) getActivity().findViewById(R.id.linearlayout_empty_state);
        setupRecycleView();
        setupSpinner();

/*        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(ID_LOADER, null, DataFragment.this); // FIXME: 4/3/2016 It is necessary to save a position of spinner after refresh
            }
        });*/
    }

    private void setupSpinner() {
        spinner = (Spinner) getActivity().findViewById(R.id.toolbar_spinner);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: position = " + position);
        lastItemPosition = currentPositionItemSpinner;
        currentPositionItemSpinner = position;
        switchSpinner(position);
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
            presenter.onClickLastMouth();
        } else if (position == 4) {
            presenter.onClickHistory();
        } else if (position == POSITION_TODAY) {
            presenter.onClickToday();
        }
        if (spinner.getSelectedItemPosition() != position) {
            spinner.setSelection(position);
        }
    }

    private void setupRecycleView() {
        RecyclerView recyclerView = (RecyclerView) getActivity()
                .findViewById(R.id.recycler_view_used_apps);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));

        adapter = new ApplicationsUsedAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        presenter.bindView(this);
        spinner.setOnItemSelectedListener(this);
        getLoaderManager().initLoader(ID_LOADER, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        presenter.unbindView();
        spinner.setOnItemSelectedListener(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void displayData(List<ApplicationUsed> list) {
        adapter.clearAndAddAll(list);
    }

    @Override
    public void showStateProcessing() {
        Log.d(TAG, "showStateProcessing: lastItemPosition = " + lastItemPosition);
        //spinner.setSelection(lastItemPosition);
        Snackbar.make(getActivity().findViewById(R.id.container), "Data processing. Please, wait",
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showQuantityOfData(int quantity) {
        Snackbar.make(getActivity().findViewById(R.id.container), "Loaded items " + quantity,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void displayEmptyState(boolean isVisible) {
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("current_item_spinner", currentPositionItemSpinner);
        Log.d(TAG, "onSaveInstanceState: currentPositionItemSpinner = " + currentPositionItemSpinner);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPositionItemSpinner = savedInstanceState.getInt("current_item_spinner", 0);
            Log.d(TAG, "onViewStateRestored: currentPositionItemSpinner = " + currentPositionItemSpinner);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<List<ApplicationUsed>> loader, List<ApplicationUsed> data) {
        Log.d(TAG, "onLoadFinished: currentPositionItemSpinner = " + currentPositionItemSpinner);
        presenter.onLoadFinished(data);
        switchSpinner(currentPositionItemSpinner);
/*        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }*/
    }

    @Override
    public void onLoaderReset(Loader<List<ApplicationUsed>> loader) {
        Log.d(TAG, "onLoaderReset: ");
    }
}