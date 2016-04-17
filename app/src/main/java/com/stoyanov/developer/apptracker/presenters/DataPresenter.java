package com.stoyanov.developer.apptracker.presenters;

import android.os.Handler;
import android.util.Log;

import com.stoyanov.developer.apptracker.models.ApplicationUsed;
import com.stoyanov.developer.apptracker.views.DataView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class DataPresenter extends BasePresenter<List<ApplicationUsed>, DataView> {

    private static final String TAG = "ListOfAppsPresenter";

    private ArrayList<ApplicationUsed> processedData;
    private boolean isProcessingData = false;

    public DataPresenter() {
        setModel(new ArrayList<ApplicationUsed>());
        processedData = new ArrayList<>();
    }

    @Override
    protected void updateView() {
    }

    @Override
    protected void resetState() {
        if (setupDone()) {
            view().displayEmptyState(false);
            view().displayData(Collections.<ApplicationUsed>emptyList());
        }
    }

    public void onLoadFinished(List<ApplicationUsed> data) {
        model.clear();
        model.addAll(data);
    }

    public void onClickHistory() {
        ProcessingThread thread = new ProcessingThread() {
            @Override
            void performLongOperation() {
                processedData.clear();
                processedData.addAll(model);
            }
        };
        thread.setSorting(false);
        thread.start();
    }

    public void onClickAllTime() {
        new ProcessingThread() {
            @Override
            void performLongOperation() {
                processedData.clear();
                for (ApplicationUsed item : model) {
                    ApplicationUsed newInstance = new ApplicationUsed(item);
                    processedData.add(newInstance);
                }
                ArrayList<ApplicationUsed> result = calculateSpendTime(processedData);
                processedData.clear();
                processedData.addAll(result);
            }
        }.start();
    }

    public void onClickLastMonth() {
        new ProcessingThread() {
            @Override
            void performLongOperation() {
                Calendar nowCalendar = Calendar.getInstance();
                nowCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                int nowMonth = nowCalendar.get(Calendar.MONTH);
                Log.d(TAG, "performLongOperation: nowMonth == " + nowMonth);

                processedData.clear();
                for (ApplicationUsed element : model) {
                    ApplicationUsed newInstance = new ApplicationUsed(element);

                    Calendar item = Calendar.getInstance();
                    nowCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                    item.setTime(newInstance.getDate());

                    Log.d(TAG, "performLongOperation: item.get(Calendar.MONTH) == "
                            + item.get(Calendar.MONTH));

                    if (nowMonth == item.get(Calendar.MONTH)) {
                        processedData.add(newInstance);
                    }
                }
                ArrayList<ApplicationUsed> result = calculateSpendTime(processedData);
                processedData.clear();
                processedData.addAll(result);
            }
        }.start();
    }

    public void onClickLastWeek() {
        new ProcessingThread() {
            @Override
            void performLongOperation() {
                Calendar now = Calendar.getInstance();
                now.setFirstDayOfWeek(Calendar.MONDAY);
                int nowWeekOfYear = now.get(Calendar.WEEK_OF_YEAR);
                Log.d(TAG, "performLongOperation: nowWeekOfYear == " + nowWeekOfYear);

                processedData.clear();
                for (ApplicationUsed item : model) {
                    ApplicationUsed newInstance = new ApplicationUsed(item);

                    Calendar itemCalendar = Calendar.getInstance();
                    itemCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                    itemCalendar.setTime(newInstance.getDate());

                    if (nowWeekOfYear == itemCalendar.get(Calendar.WEEK_OF_YEAR)) {
                        processedData.add(newInstance);
                    }
                }
                ArrayList<ApplicationUsed> result = calculateSpendTime(processedData);
                processedData.clear();
                processedData.addAll(result);
            }
        }.start();
    }

    public void onClickToday() {
        new ProcessingThread() {
            @Override
            void performLongOperation() {
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
                ArrayList<ApplicationUsed> result = calculateSpendTime(processedData);
                processedData.clear();
                processedData.addAll(result);
            }
        }.start();
    }

    private ArrayList<ApplicationUsed> calculateSpendTime(List<ApplicationUsed> list) {
        ArrayList<ApplicationUsed> bufferList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ApplicationUsed app = list.get(i);
            if (!contains(bufferList, app)) {
                for (int t = i + 1; t < list.size(); t++) {
                    if (app.getApplicationName().equals(list.get(t).getApplicationName())) {
                        app.setSpendTime(app.getTimeSpent() + list.get(t).getTimeSpent());
                    }
                }
                bufferList.add(app);
            }
        }
        return bufferList;
    }

    private boolean contains(ArrayList<ApplicationUsed> list, ApplicationUsed app) {
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            if (app.getApplicationName().equals(list.get(i).getApplicationName())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public abstract class ProcessingThread {

        private boolean isSorting = true;

        public void start() {
            if (!isProcessingData) {
                isProcessingData = true;
                resetState();
                view().displayProgress(true);
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        performLongOperation();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (setupDone()) {
                                    if (processedData.size() > 0) {
                                        if (isSorting) {
                                            Collections.sort(processedData);
                                        }
                                        view().showQuantityOfData(processedData.size());
                                    } else {
                                        view().displayEmptyState(true);
                                    }
                                    view().displayData(processedData);
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

        public void setSorting(boolean isSorting) {
            this.isSorting = isSorting;
        }

        abstract void performLongOperation();
    }
}
