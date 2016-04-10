package com.stoyanov.developer.apptracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.stoyanov.developer.apptracker.database.dao.ApplicationDAO;
import com.stoyanov.developer.apptracker.database.dao.entity.Application;

import java.util.Date;

public class ScreenLockReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenLockReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d(TAG, "onReceive: Screen is locked");
            save(context);
        }
    }

    private void save(Context context) {
        ApplicationDAO dao = new ApplicationDAO(context);
        try {
            dao.open();

            Application saveApp = new Application();
            saveApp.setAppPackage("screen lock"); // FIXME: 4/5/2016
            saveApp.setDatetime(new Date());
            dao.create(saveApp);
        } finally {
            dao.close();
        }
    }

}
