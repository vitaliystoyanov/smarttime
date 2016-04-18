package com.stoyanov.developer.apptracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.stoyanov.developer.apptracker.ApplicationsUsedLoader;
import com.stoyanov.developer.apptracker.R;
import com.stoyanov.developer.apptracker.TimeConverter;
import com.stoyanov.developer.apptracker.adapters.StringSpinnerAdapter;
import com.stoyanov.developer.apptracker.models.ApplicationUsed;
import com.stoyanov.developer.apptracker.presenters.ChartPresenter;
import com.stoyanov.developer.apptracker.views.ChartView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartsFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        ChartView, LoaderManager.LoaderCallbacks<List<ApplicationUsed>>, Updatable {

    public static final String TAG = "ChartsFragment";

    private final static String[] days = new String[]{"Sun", "Mon", "Tue", "Wen", "Thu", "Fri", "Sat",};
    public static final String EXTRA_CURRENT_ITEM_SPINNER = "EXTRA_CURRENT_ITEM_SPINNER";
    private static final int ID_LOADER_CHARTS = 2;
    private static final int ITEM_POSITION_TODAY = 0;
    private static final int ITEM_POSITION_LAST_WEEK = 1;
    private static final int ITEM_POSITION_ALL_TIME = 2;
    public static final int INTRO_CHART_ANIMATION_DURATION = 250;

    private boolean isFirstItemSpinnerSelected;
    private int currentItemSpinner;
    private ColumnChartView columnChart;
    private LinearLayout layoutEmptyState;
    private LineChartView lineChart;
    private ChartPresenter presenter;
    private TextView totalSpentTime;
    private Spinner spinner;
    private LineChartData lineChartData;
    private ColumnChartData columnChartData;
    private Animation animationScale;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        animationScale = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.scale_up);
        layoutEmptyState = (LinearLayout) getActivity().findViewById(R.id.linearlayout_empty_state_charts);
        totalSpentTime = (TextView) getActivity().findViewById(R.id.textview_total_spent_time);
        setupCharts();
        setupSpinner();
        presenter = new ChartPresenter();
        presenter.bindView(this);
    }

    private void setupCharts() {
        lineChart = (LineChartView) getActivity().findViewById(R.id.chart_line);
        columnChart = (ColumnChartView) getActivity().findViewById(R.id.chart_column);
        columnChart.setValueSelectionEnabled(false);
        columnChart.setZoomEnabled(false);
        lineChart.setViewportCalculationEnabled(false);
        lineChart.setZoomEnabled(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);

        createLineChart();
        createColumnChart();
    }

    private void createLineChart() {

        List<Line> lines = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        for (int j = 0; j < 24; j++) {
            values.add(new PointValue(j, 0));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN);
        line.setFilled(true);
        line.setHasPoints(true);
        line.setPointRadius(4);
        lines.add(line);

        lineChartData = new LineChartData(lines);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Time (hours)");
        axisY.setName("Spent time (minutes)");
        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);

        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChart.setLineChartData(lineChartData);
    }

    private void createColumnChart() {
        int numColumns = 7;
        List<Column> columns = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i < numColumns; ++i) {
            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.nextColor()));
            axisValues.add(new AxisValue(i).setLabel(days[i]));

            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
        }

        columnChartData = new ColumnChartData(columns);

        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName(null);
        axisY.setName("Spent time (minutes)");
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);

        columnChart.setColumnChartData(columnChartData);
    }

    private void setupSpinner() {
        StringSpinnerAdapter adapter = new StringSpinnerAdapter(getActivity());
        adapter.addItems(getResources().getStringArray(R.array.spinner_items_charts));
        spinner = (Spinner) getActivity().findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(null);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        spinner.setOnItemSelectedListener(this);
        getLoaderManager().initLoader(ID_LOADER_CHARTS, null, this);
        onUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        spinner.setOnItemSelectedListener(null);
        isFirstItemSpinnerSelected = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        presenter.unbindView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: position = " + position
                + "isFirstItemSpinnerSelected ==" + isFirstItemSpinnerSelected);
        if (isFirstItemSpinnerSelected) {
            currentItemSpinner = position;
            switchItem(position);
        }
        isFirstItemSpinnerSelected = true;
    }

    private void switchItem(int position) {
        Log.d(TAG, "switchItem: position = " + position);
        if (position == ITEM_POSITION_TODAY) {
            presenter.onClickToday();
        } else if (position == ITEM_POSITION_LAST_WEEK) {
            presenter.onClickLastWeek();
        } else if (position == ITEM_POSITION_ALL_TIME) {
            presenter.onClickAllTime();
            Snackbar.make(getActivity().findViewById(R.id.container), "In developing",
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void displayToday(int[] data) {
        for (int aData : data) {
            Log.d(TAG, "displayToday: item - " + aData);
        }
        invisibleAllCharts();
        lineChart.setVisibility(View.VISIBLE);
        drawLineChart(data);
    }

    private void resetViewportLineChart(int maxTop) {
        final Viewport viewport = new Viewport(lineChart.getMaximumViewport());
        viewport.bottom = 0;
        viewport.top = maxTop;
        viewport.left = 0;
        viewport.right = 25;
        lineChart.setMaximumViewport(viewport);
        lineChart.setCurrentViewport(viewport);
    }

    private void drawLineChart(int[] data) {
        lineChart.cancelDataAnimation();
        resetViewportLineChart(getMax(data));
        Line line = lineChartData.getLines().get(0);
        for (int i = 0; i < line.getValues().size(); i++) {
            PointValue value = line.getValues().get(i);
            value.setTarget(value.getX(), data[i]);
        }
        lineChart.startDataAnimation(INTRO_CHART_ANIMATION_DURATION);
    }

    public void drawColumnChart(int[] data) {
        columnChart.cancelDataAnimation();
        List<Column> columns = columnChartData.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            column.getValues().get(0).setTarget(data[i]);
        }
        columnChart.startDataAnimation(INTRO_CHART_ANIMATION_DURATION);
    }

    private int getMax(int[] data) {
        int max = 0;
        for (int element : data) {
            if (max < element) {
                max = element;
            }
        }
        return max;
    }

    @Override
    public void displayLastWeek(int[] data) {
        for (int i = 0; i < data.length; i++) {
            Log.d(TAG, "displayLastWeek: [" + i + "] " + " spent minutes = " + data[i]);
        }
        invisibleAllCharts();
        columnChart.setVisibility(View.VISIBLE);
        drawColumnChart(data);
    }


    private void invisibleAllCharts() {
        lineChart.setVisibility(View.INVISIBLE);
        columnChart.setVisibility(View.INVISIBLE);
    }

    @Override
    public void displayTotalTimeSpent(int minutes) {
        if (minutes != 0) {
            String text = "Total time spent: " + TimeConverter.convertWithoutSeconds(minutes);
            totalSpentTime.setText(text);
        } else {
            totalSpentTime.setText("Total time spent: 0");
        }
    }

    @Override
    public void displayProgress(boolean state) {
        Log.d(TAG, "displayProgress: ");
    }

    @Override
    public void showStateProcessing() {
        Log.d(TAG, "showStateProcessing: ");
    }

    @Override
    public void displayEmptyState(boolean state) { // FIXME: 4/18/2016 operator
        if (state) {
            totalSpentTime.setVisibility(View.INVISIBLE);
            lineChart.setVisibility(View.INVISIBLE);
            columnChart.setVisibility(View.INVISIBLE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            layoutEmptyState.startAnimation(animationScale);
        } else {
            totalSpentTime.setVisibility(View.VISIBLE);
            lineChart.setVisibility(View.VISIBLE);
            columnChart.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public Loader<List<ApplicationUsed>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        return new ApplicationsUsedLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ApplicationUsed>> loader, List<ApplicationUsed> data) {
        Log.d(TAG, "onLoadFinished: currentItemSpinner == " + currentItemSpinner);
        presenter.onLoadFinished(data);
        spinner.setSelection(currentItemSpinner);
        switchItem(currentItemSpinner);
    }

    @Override
    public void onLoaderReset(Loader<List<ApplicationUsed>> loader) {
        Log.d(TAG, "onLoaderReset: ");
    }

    @Override
    public void onUpdate() {
        Log.d(TAG, "onUpdate: ");
        getLoaderManager().restartLoader(ID_LOADER_CHARTS, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: currentItemSpinner = " + currentItemSpinner);
        outState.putInt(EXTRA_CURRENT_ITEM_SPINNER, currentItemSpinner);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            currentItemSpinner = savedInstanceState.getInt(EXTRA_CURRENT_ITEM_SPINNER);
            Log.d(TAG, "onViewStateRestored: currentItemSpinner = " + currentItemSpinner);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }
}
