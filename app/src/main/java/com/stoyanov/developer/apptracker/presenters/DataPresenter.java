package com.stoyanov.developer.apptracker.presenters;

import android.os.Handler;
import android.util.Log;

import com.stoyanov.developer.apptracker.models.ApplicationUsed;
import com.stoyanov.developer.apptracker.views.DataView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
        new ProcessingThread() {
            @Override
            void performLongOperation() {
                processedData.clear();
                processedData.addAll(model);
            }
        }.start();
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

    public void onClickLastMouth() {
        new ProcessingThread() {
            @Override
            void performLongOperation() {
                Date now = new Date();
                Calendar nowCalendar = Calendar.getInstance();
                nowCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                nowCalendar.setTime(now);
                int nowMonth = nowCalendar.get(Calendar.MONTH);
                Log.d(TAG, "performLongOperation: nowMonth == " + nowMonth);

                processedData.clear();
                for (ApplicationUsed item : model) {
                    ApplicationUsed newInstance = new ApplicationUsed(item);

                    Date itemDate = newInstance.getDate();
                    Calendar itemCalendar = Calendar.getInstance();
                    itemCalendar.setTime(itemDate);

                    Log.d(TAG, "performLongOperation: itemCalendar.get(Calendar.MONTH) == "
                            + itemCalendar.get(Calendar.MONTH));

                    if (nowMonth == itemCalendar.get(Calendar.MONTH)) {
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
                Date now = new Date();
                Calendar nowCalendar = Calendar.getInstance();
                nowCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                nowCalendar.setTime(now);
                int nowWeekOfYear = nowCalendar.get(Calendar.WEEK_OF_YEAR);
                Log.d(TAG, "performLongOperation: nowWeekOfYear == " + nowWeekOfYear);

                processedData.clear();
                for (ApplicationUsed item : model) {
                    ApplicationUsed newInstance = new ApplicationUsed(item);

                    Date itemDate = newInstance.getDate();
                    Calendar itemCalendar = Calendar.getInstance();
                    itemCalendar.setTime(itemDate);

                    Log.d(TAG, "performLongOperation: itemCalendar.get(Calendar.WEEK_OF_YEAR) == "
                            + itemCalendar.get(Calendar.WEEK_OF_YEAR));

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
                Date now = new Date();
                processedData.clear();
                for (int i = 0; i < model.size(); i++) {
                    ApplicationUsed item = new ApplicationUsed(model.get(i));
                    Date itemDate = item.getDate();
                    if (now.getYear() == itemDate.getYear()
                            && now.getMonth() == itemDate.getMonth()
                            && now.getDay() == itemDate.getDay()) {
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
                    if (app.getAppName().equals(list.get(t).getAppName())) {
                        app.setSpendTime(app.getSpendTime() + list.get(t).getSpendTime());
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
            if (app.getAppName().equals(list.get(i).getAppName())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public abstract class ProcessingThread {

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

        abstract void performLongOperation();
    }
}
