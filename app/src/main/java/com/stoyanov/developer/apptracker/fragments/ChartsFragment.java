package com.stoyanov.developer.apptracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
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

    private static String[] days;
    public static final String EXTRA_CURRENT_ITEM_SPINNER = "EXTRA_CURRENT_ITEM_SPINNER";
    private static final int ID_LOADER_CHARTS = 2;
    private static final int ITEM_POSITION_TODAY = 0;
    private static final int ITEM_POSITION_LAST_WEEK = 1;
    private static final int ITEM_POSITION_ALL_TIME = 2;
    public static final int INTRO_CHART_ANIMATION_DURATION = 200;

    private boolean isFirstItemSpinnerSelected;
    private int currentItemSpinner;
    private ColumnChartView columnChart;
    private RelativeLayout layoutEmptyState;
    private LineChartView lineChart;
    private ChartPresenter presenter;
    private TextView totalSpentTime;
    private Spinner spinner;
    private LineChartData lineChartData;
    private ColumnChartData columnChartData;
    private Animation animationScale;
    private String[] months;
    private CircularProgressView progressBarView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        days = getResources().getStringArray(R.array.chart_axis_days);
        months = getResources().getStringArray(R.array.chart_axis_months);
        animationScale = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.scale_up);
        progressBarView = (CircularProgressView) getActivity().findViewById(R.id.progress_view);
        layoutEmptyState = (RelativeLayout) getActivity().findViewById(R.id.layout_empty_state_charts);
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
        axisX.setName(getResources().getString(R.string.chart_axis_time_hours));
        axisY.setName(getResources().getString(R.string.chart_axis_spent_time));
        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);

        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChart.setLineChartData(lineChartData);
    }

    private void createColumnChartDays() {
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
        axisY.setName(getResources().getString(R.string.chart_axis_spent_time));
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);

        columnChart.setColumnChartData(columnChartData);
    }

    private void createColumnChartMonths() {
        int numColumns = 12;
        List<Column> columns = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i < numColumns; ++i) {
            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.nextColor()));
            axisValues.add(new AxisValue(i).setLabel(months[i]));

            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
        }

        columnChartData = new ColumnChartData(columns);

        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName(null);
        axisY.setName(getResources().getString(R.string.chart_axis_spent_time));
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
        isFirstItemSpinnerSelected = false;
        spinner.setOnItemSelectedListener(null);
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
                + ", isFirstItemSpinnerSelected == " + isFirstItemSpinnerSelected);
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
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void resetViewportLineChart(int maximumTop) {
        final Viewport viewport = new Viewport(lineChart.getMaximumViewport());
        viewport.bottom = 0;
        viewport.top = maximumTop;
        viewport.left = 0;
        viewport.right = 23;
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
        convertForChart(data);
        createColumnChartDays();
        invisibleAllCharts();
        columnChart.setVisibility(View.VISIBLE);
        drawColumnChart(data);
    }

    @Override
    public void displayToday(int[] data) {
        convertForChart(data);
        invisibleAllCharts();
        lineChart.setVisibility(View.VISIBLE);
        drawLineChart(data);
    }


    private void invisibleAllCharts() {
        lineChart.setVisibility(View.INVISIBLE);
        columnChart.setVisibility(View.INVISIBLE);
    }

    @Override
    public void displayAllTime(int[] data) {
        convertForChart(data);
        createColumnChartMonths();
        invisibleAllCharts();
        columnChart.setVisibility(View.VISIBLE);
        drawColumnChart(data);
    }

    private void convertForChart(int[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = TimeConverter.convertToMinutes(data[i]);
            Log.d(TAG, " [" + i + "] " + " spent minutes = " + data[i]);
        }
    }

    @Override
    public void displayTotalTimeSpent(int seconds) { // TODO refactoring
        if (seconds != 0) {
            String text = getResources().getString(R.string.filed_total_spent_time) +
                    " " + TimeConverter.convert(seconds);
            totalSpentTime.setText(text);
        } else {
            totalSpentTime.setText(R.string.field_total_spent_time_zero);
        }
    }

    @Override
    public void displayProgress(boolean state) {
        progressBarView.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showStateProcessing() {
        Log.d(TAG, "showStateProcessing: ");
    }

    @Override
    public void displayEmptyState(boolean state) { // FIXME: 4/18/2016 operator
        Log.d(TAG, "displayEmptyState: state = " + state);
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
        Log.d(TAG, "onLoadFinished: currentItemSpinner == " + currentItemSpinner
                + ", data size  = " + data.size());
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
}
