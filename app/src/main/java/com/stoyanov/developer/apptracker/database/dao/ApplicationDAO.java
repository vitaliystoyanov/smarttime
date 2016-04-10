package com.stoyanov.developer.apptracker.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.stoyanov.developer.apptracker.database.dao.entity.Application;
import com.stoyanov.developer.apptracker.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ApplicationDAO extends SQLiteDAO implements DAOInterface<Application> {

    private static final String TAG = "ApplicationDAO";

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());
    private static final String WHERE_ID_EQUALS = DatabaseHelper.ID_COLUMN + " =?";

    public ApplicationDAO(Context context) {
        super(context);
    }

    @Override
    public long create(Application application) {
        Log.d(TAG, "create: Instance of application = " + application.toString());
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PACKAGE_APP_COLUMN, application.getAppPackage());
        values.put(DatabaseHelper.DATETIME_COLUMN, formatter.format(application.getDatetime()));
        values.put(DatabaseHelper.SPEND_TIME_COLUMN, application.getSpendTime());
        return database.insert(DatabaseHelper.APPLICATIONS_TABLE, null, values);
    }

    @Override
    public int delete(Application application) {
        Log.d(TAG, "delete: Instance of application = " + application.toString());
        return database.delete(DatabaseHelper.APPLICATIONS_TABLE, WHERE_ID_EQUALS,
                new String[]{String.valueOf(application.getId())});
    }

    @Override
    public long update(Application application) {
        Log.d(TAG, "update: Instance of application = " + application.toString());
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PACKAGE_APP_COLUMN, application.getAppPackage());
        values.put(DatabaseHelper.DATETIME_COLUMN, formatter.format(application.getDatetime()));
        return (long) database.update(DatabaseHelper.APPLICATIONS_TABLE, values, WHERE_ID_EQUALS,
                new String[]{String.valueOf(application.getId())});
    }

    public long updateSpendTime(Application application) {
        Log.d(TAG, "update: Instance of application = " + application.toString());
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SPEND_TIME_COLUMN, application.getSpendTime());
        return (long) database.update(DatabaseHelper.APPLICATIONS_TABLE, values, WHERE_ID_EQUALS,
                new String[]{String.valueOf(application.getId())});
    }

    @Override
    public ArrayList<Application> retrieveAll() {
        ArrayList<Application> applications = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(DatabaseHelper.APPLICATIONS_TABLE,
                    new String[]{DatabaseHelper.ID_COLUMN,
                            DatabaseHelper.PACKAGE_APP_COLUMN,
                            DatabaseHelper.DATETIME_COLUMN, DatabaseHelper.SPEND_TIME_COLUMN},
                    null, null, null, null, null);

            while (cursor.moveToNext()) {
                int idColumnIndex = cursor.getColumnIndex(DatabaseHelper.ID_COLUMN);
                int packageAppColumnIndex = cursor.getColumnIndex(DatabaseHelper.PACKAGE_APP_COLUMN);
                int dateTimeColumnIndex = cursor.getColumnIndex(DatabaseHelper.DATETIME_COLUMN);
                int spendTimeColumnIndex = cursor.getColumnIndex(DatabaseHelper.SPEND_TIME_COLUMN);

                Application application = new Application();
                application.setId(cursor.getInt(idColumnIndex));
                application.setAppPackage(cursor.getString(packageAppColumnIndex));
                application.setSpendTime(cursor.getInt(spendTimeColumnIndex));

                try {
                    application.setDatetime(formatter.parse(cursor.getString(dateTimeColumnIndex)));
                } catch (java.text.ParseException e) {
                    application.setDatetime(null);
                    Log.e(TAG, "retrieveAll: Can not parse datetime from a cursor", e);
                }
                applications.add(application);
            }
            Log.d(TAG, "retrieveAll: List of the Application objects has got such size = " + applications.size());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return applications;
    }

    @Override
    public boolean deleteAll() {
        return database.delete(DatabaseHelper.APPLICATIONS_TABLE, null, null) > 0;
    }
}
