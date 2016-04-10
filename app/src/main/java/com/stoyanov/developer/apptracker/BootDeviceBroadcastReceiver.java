package com.stoyanov.developer.apptracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.stoyanov.developer.apptracker.service.TrackerService;

public class BootDeviceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootDevice";

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO it is necessary to fix a exception
        Intent startTrackerService = new Intent(context, TrackerService.class);
        Log.d(TAG, "onReceive: Start tracker service...");
        //context.startService(startTrackerService);
    }
}
