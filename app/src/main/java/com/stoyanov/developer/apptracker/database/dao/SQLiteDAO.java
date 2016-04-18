package com.stoyanov.developer.apptracker.database.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.stoyanov.developer.apptracker.database.DatabaseHelper;

public class SQLiteDAO {

    private static final String TAG = "SQLiteDAO";

    protected static SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context context;

    public SQLiteDAO(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(this.context);
        open();
    }

    public void open() throws SQLException {
        if (dbHelper == null) {
            dbHelper = DatabaseHelper.getInstance(context);
        }
        if (database == null) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        Log.d(TAG, "close: ");
        //dbHelper.close();
        //database = null;
    }
}
