package com.stoyanov.developer.apptracker;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.stoyanov.developer.apptracker.database.dao.ApplicationDAO;
import com.stoyanov.developer.apptracker.database.dao.entity.Application;
import com.stoyanov.developer.apptracker.models.ApplicationUsed;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ApplicationsUsedLoader extends AsyncTaskLoader<List<ApplicationUsed>> {

    private static final String TAG = "ApplicationsUsedLoader";

    private List<ApplicationUsed> data;

    public ApplicationsUsedLoader(Context context) {
        super(context);
    }

    @Override
    public List<ApplicationUsed> loadInBackground() {
        Log.d(TAG, "loadInBackground: ");
        ApplicationDAO dao = new ApplicationDAO(getContext());
        ArrayList<ApplicationUsed> list = new ArrayList<>();
        try {
            ArrayList<Application> retrievedApps = dao.retrieveAll();
            for (int i = retrievedApps.size() - 1; i >= 0; i--) {
                Application item = retrievedApps.get(i);

                ApplicationUsed instance = new ApplicationUsed();
                instance.setId(item.getId());
                instance.setAppName(item.getAppPackage());
                instance.setSpendTime(item.getSpendTime());
                instance.setDate(item.getDatetime());

                list.add(instance);
            }
        } finally {
            dao.close();
        }
        return list;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading: ");
        if (data != null) {
            Log.d(TAG, "onStartLoading: data != null");
            deliverResult(data);
        } else {
            Log.d(TAG, "onStartLoading: forceLoad()");
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<ApplicationUsed> data) {
        Log.d(TAG, "deliverResult: ");
        this.data = data;

        if (isStarted()) {
            Log.d(TAG, "deliverResult: super.deliverResult(data)");
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStopLoading() {
        Log.d(TAG, "onStopLoading: ");
        cancelLoad();
    }

    @Override
    protected void onReset() {
        Log.d(TAG, "onReset: ");
        onStopLoading();
        if (data != null) {
            Log.d(TAG, "onReset: data != null");
            data = null;
        }
    }
}
