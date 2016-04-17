package com.stoyanov.developer.apptracker.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.stoyanov.developer.apptracker.MainActivity;
import com.stoyanov.developer.apptracker.R;
import com.stoyanov.developer.apptracker.database.dao.ApplicationDAO;
import com.stoyanov.developer.apptracker.database.dao.entity.Application;

import java.util.Date;
import java.util.List;

public class TrackerService extends Service implements TrackerIterface {

    private static final String TAG = "TrackerService";

    private static final int ID_NOTIFICATION = 15;
    private static final int TIME_DELAY = 1000;

    private NotificationManager notificationManager;
    private ActivityManager activityManager;
    private Notification notification;
    private boolean runningThread = true;
    private Thread thread;

    public TrackerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        notification = createNotification("Unknown");
        showNotification();

        initThread();
    }

    private void initThread() { // TODO It is necessary to refactoring this method
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String lastApp = "";
                String newApp;
                String thisPackageApp = getPackageName();
                int spendSeconds = 0;
                int idApp = -1;
                while (runningThread) {
                    newApp = getRunningApplication();
                    if (!lastApp.equals(newApp) && !newApp.equals(thisPackageApp)) {
                        if (idApp != -1) {
                            Application updateApp = new Application();
                            updateApp.setId(idApp);
                            updateApp.setSpendTime(spendSeconds);
                            update(updateApp);
                        }
                        spendSeconds = 0;
                        idApp = (int) save(newApp);
                        Log.d(TAG, "run: This instance app will be updated (idApp = " + idApp + ")");
                        lastApp = newApp; //TODO Here may be a memory leak
                    }
                    try {
                        Thread.sleep(TIME_DELAY);
                        spendSeconds++;
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: Thread is interrupted!", e);
                    }
                }
            }
        });
        thread.start();
    }

    private Notification createNotification(String text) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        return new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Application Tracker")
                .setContentText(text)
                .setContentIntent(contentIntent)
                .build();
    }

    private void showNotification() {
        startForeground(ID_NOTIFICATION, notification);
    }

    @Override
    public String getRunningApplication() {
        List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
        if (list.size() > 0) {
            Log.d(TAG, "getRunningApplication: id = " + list.get(0).processName);
            //startForeground(ID_NOTIFICATION, createNotification(list.get(0).processName));
            return list.get(0).processName;
        }
        return null;
    }

    @Override
    public long save(String processName) {
        ApplicationDAO dao = new ApplicationDAO(getApplicationContext());
        try {
            dao.open();

            Application saveApp = new Application();
            saveApp.setAppPackage(processName);
            saveApp.setDatetime(new Date()); //TODO It is necessary to think about how to do better
            return dao.create(saveApp);
        } finally {
            dao.close();
        }
    }

    public void update(Application application) {
        ApplicationDAO dao = new ApplicationDAO(getApplicationContext());
        try {
            dao.open();
            dao.updateSpendTime(application);
        } finally {
            dao.close();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved: ");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        notificationManager.cancel(ID_NOTIFICATION);

        runningThread = false;
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}
