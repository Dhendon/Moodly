package com.hendon.moodsy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_MOOD = "mood";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CREATED_DATE = "created_date";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_DESCRIPTION = "description";
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MOOD + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CREATED_DATE + " integer, " // datetime
            + COLUMN_RATING + " integer, "
            + COLUMN_DESCRIPTION + " text )";
    private static final String DATABASE_NAME = "mood.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD);
        onCreate(db);
    }

}