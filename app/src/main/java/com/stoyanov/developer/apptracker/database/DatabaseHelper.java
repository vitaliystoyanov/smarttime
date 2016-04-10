package com.stoyanov.developer.apptracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "trackerdb";
    private static final int DATABASE_VERSION = 3;

    public static final String APPLICATIONS_TABLE = "applications";

    public static final String ID_COLUMN = "id";
    public static final String PACKAGE_APP_COLUMN = "package";
    public static final String DATETIME_COLUMN = "date_time";
    public static final String SPEND_TIME_COLUMN = "spend_time";


    private static DatabaseHelper instance;

    private static final String QUERY_CREATE_APP_TABLE = "CREATE TABLE "
            + APPLICATIONS_TABLE + "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PACKAGE_APP_COLUMN + " TEXT, " + DATETIME_COLUMN + " DATE,"
            + SPEND_TIME_COLUMN + " INTEGER)";

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating tables: " + QUERY_CREATE_APP_TABLE);
        db.execSQL(QUERY_CREATE_APP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: The database is upgraded to a new version." +
                " Old version = " + oldVersion + ", new version = " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + APPLICATIONS_TABLE);
        onCreate(db);
    }
}
