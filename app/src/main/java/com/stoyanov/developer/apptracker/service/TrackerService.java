package com.stoyanov.developer.apptracker.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
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
    private boolean runningThread;
    private Thread thread;

    public TrackerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initService();
    }

    private void initService() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        notification = createNotification(getString(R.string.notification_content_text));
        showNotification();

        initThread();
    }

    private void initThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runningThread = true;
                String lastApp = "";
                String newApp;
                String thisPackageApp = getPackageName();
                int spendSeconds = 0;
                int idApp = -1;
                while (runningThread) {
                    newApp = getIDRunningApp();
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
                        lastApp = newApp;
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
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_time_spent)
                .setTicker(getString(R.string.message_tracker_start))
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .build();
    }

    private void showNotification() {
        startForeground(ID_NOTIFICATION, notification);
    }

    @Override
    public String getIDRunningApp() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
            if (tasks.size() > 0) {
                Log.d(TAG, "getIDRunningApp: (version <= 19) topActivity - " + tasks.get(0).topActivity.getPackageName());
                return tasks.get(0).topActivity.getPackageName();
            }
        } else {
            List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
            if (list.size() > 0) {
                Log.d(TAG, "getIDRunningApp: (version => 20) id = " + list.get(0).processName);
                return list.get(0).processName;
            }
        }
        return null;
    }

    @Override
    public long save(String processName) {
        Application saveApp = new Application();
        saveApp.setAppPackage(processName);
        saveApp.setDatetime(new Date());
        ApplicationDAO dao = new ApplicationDAO(getApplicationContext());
        try {
            dao.open();
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
        initThread();
        return START_STICKY;
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
