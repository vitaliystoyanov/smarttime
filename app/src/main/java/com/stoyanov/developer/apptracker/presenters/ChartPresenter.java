package com.stoyanov.developer.apptracker.presenters;

import android.os.Handler;
import android.util.Log;

import com.stoyanov.developer.apptracker.TimeConverter;
import com.stoyanov.developer.apptracker.models.ApplicationUsed;
import com.stoyanov.developer.apptracker.views.ChartView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartPresenter extends BasePresenter<List<ApplicationUsed>, ChartView> {

    private static final String TAG = "ChartPresenter";

    private ArrayList<ApplicationUsed> processedData;
    private boolean isProcessingData;

    public ChartPresenter() {
        setModel(new ArrayList<ApplicationUsed>());
        processedData = new ArrayList<>();
    }

    @Override
    protected void updateView() {
    }

    public void onClickToday() {
        Log.d(TAG, "onClickToday: model.size = " + model.size());
        new ProcessingThread() {

            @Override
            int[] performLongOperation() {
                int[] data = new int[24];
                Calendar now = Calendar.getInstance();
                now.setFirstDayOfWeek(Calendar.MONDAY);
                int nowDay = now.get(Calendar.DAY_OF_YEAR);
                processedData.clear();
                for (ApplicationUsed element : model) {
                    ApplicationUsed item = new ApplicationUsed(element);

                    Calendar itemDate = Calendar.getInstance();
                    itemDate.setFirstDayOfWeek(Calendar.MONDAY);
                    itemDate.setTime(item.getDate());

                    if (nowDay == itemDate.get(Calendar.DAY_OF_YEAR)) {
                        Log.d(TAG, "onClickToday: item added - " + item.toString());
                        processedData.add(item);
                    }
                }
                for (int i = 0; i < 24; i++) {
                    for (int t = 0; t < processedData.size(); t++) {
                        if (processedData.get(t).getDate().getHours() == i) { // FIXME: 4/21/2016
                            data[i] += processedData.get(t).getSpentTime();
                        }
                    }
                }
                return data;
            }

            @Override
            void displayData(int[] data) {
                int total = getTotalSpent(data); // FIXME: 4/21/2016
                view().displayEmptyState(false);
                if (TimeConverter.convertToMinutes(total) != 0) {
                    view().displayToday(data);
                    view().displayTotalTimeSpent(total);
                } else {
                    view().displayEmptyState(true);
                }
            }
        }.start();
    }

    public void onClickLastWeek() {
        Log.d(TAG, "onClickLastWeek: ");
        new ProcessingThread() {

            @Override
            int[] performLongOperation() {
                int[] data = new int[7];
                Calendar now = Calendar.getInstance();
                now.setFirstDayOfWeek(Calendar.MONDAY);
                int nowWeekOfYear = now.get(Calendar.WEEK_OF_YEAR);

                processedData.clear();
                for (ApplicationUsed element : model) {
                    ApplicationUsed newInstance = new ApplicationUsed(element);

                    Calendar itemCalendar = Calendar.getInstance();
                    itemCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                    itemCalendar.setTime(newInstance.getDate());

                    if (nowWeekOfYear == itemCalendar.get(Calendar.WEEK_OF_YEAR)) {
                        processedData.add(newInstance);
                    }
                }

                for (ApplicationUsed element : processedData) {
                    Calendar item = Calendar.getInstance();
                    item.setFirstDayOfWeek(Calendar.MONDAY);
                    item.setTime(element.getDate());
                    Log.d(TAG, "performLongOperation: item.get(Calendar.DAY_OF_WEEK) == "
                            + item.get(Calendar.DAY_OF_WEEK) + ", " + element.toString());
                    data[item.get(Calendar.DAY_OF_WEEK) - 1] += element.getSpentTime();
                }
                return data;
            }

            @Override
            void displayData(int[] data) {
                int total = getTotalSpent(data);
                view().displayEmptyState(false);
                if (TimeConverter.convertToMinutes(total) != 0) {
                    view().displayLastWeek(data);
                    view().displayTotalTimeSpent(total);
                } else {
                    view().displayEmptyState(true);
                }
            }
        }.start();
    }

    public void onClickAllTime() {
        Log.d(TAG, "onClickAllTime: ");
        new ProcessingThread() {

            @Override
            int[] performLongOperation() {
                int[] data = new int[12];

                processedData.clear();
                for (ApplicationUsed element : model) {
                    ApplicationUsed newInstance = new ApplicationUsed(element);
                    processedData.add(newInstance);
                }

                for (ApplicationUsed element : processedData) {
                    Calendar item = Calendar.getInstance();
                    item.setFirstDayOfWeek(Calendar.MONDAY);
                    item.setTime(element.getDate());
                    Log.d(TAG, "performLongOperation: item.get(Calendar.MONTH) == "
                            + item.get(Calendar.MONTH) + ", " + element.toString());
                    data[item.get(Calendar.MONTH)] += element.getSpentTime();
                }
                return data;
            }

            @Override
            void displayData(int[] data) {
                int total = getTotalSpent(data);
                Log.d(TAG, "displayData: total = " + total);
                view().displayEmptyState(false);
                if (TimeConverter.convertToMinutes(total) != 0) {
                    view().displayAllTime(data);
                    view().displayTotalTimeSpent(total);
                } else {
                    view().displayEmptyState(true);
                }
            }
        }.start();
    }

    private int getTotalSpent(int[] data) {
        int totalSpentMinutes = 0;
        for (int i : data) {
            totalSpentMinutes += i;
        }
        Log.d(TAG, "getTotalSpent: totalSpentMinutes = " + totalSpentMinutes);
        return totalSpentMinutes;
    }

    public void onLoadFinished(List<ApplicationUsed> data) {
        Log.d(TAG, "onLoadFinished: Data size = " + data.size());
        model.clear();
        model.addAll(data);
    }

    public abstract class ProcessingThread {

        private int[] data;

        public void start() {
            if (!isProcessingData) {
                isProcessingData = true;
                resetState();
                view().displayProgress(true);
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = performLongOperation();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (setupDone()) {
                                    displayData(data);
                                    view().displayProgress(false);
                                }
                            }
                        });
                        isProcessingData = false;
                    }
                }).start();
            } else {
                view().showStateProcessing();
            }
        }

        abstract int[] performLongOperation();

        abstract void displayData(int[] data);
    }
}
