package com.hendon.moodsy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MoodDataSource {

    private final int ID_INDEX = 0;
    private final int CREATED_DATE_INDEX = 1;
    private final int RATING_INDEX = 2;
    private final int DESCRIPTION_INDEX = 3;
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_CREATED_DATE,
            DatabaseHelper.COLUMN_RATING,
            DatabaseHelper.COLUMN_DESCRIPTION};

    public MoodDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Mood createMood(int moodRating, String description) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CREATED_DATE, new Date().getTime());
        values.put(DatabaseHelper.COLUMN_RATING, moodRating);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);

        long insertId = database.insert(DatabaseHelper.TABLE_MOOD, null,
                values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_MOOD,
                allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Mood mood = cursorToMood(cursor);
        cursor.close();
        return mood;
    }

    public void deleteMood(Mood mood) {
        long id = mood.getTableID();
        System.out.println("MetricBlock deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_MOOD, DatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    /**
     * Fetches all moods from the table and returns them as a list.
     * @return Returns a list containing all the elements in the table.
     *  Returns empty list if there are no elements.
     */
    public List<Mood> getAllMoods() {
        List<Mood> moods = new ArrayList<Mood>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_MOOD,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Mood mood = cursorToMood(cursor);
            moods.add(mood);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return moods;
    }

    private Mood cursorToMood(Cursor cursor) {
        Mood mood = new Mood();
        mood.setTableID(cursor.getLong(ID_INDEX));
        long dateInMilliseconds = cursor.getLong(CREATED_DATE_INDEX);
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(dateInMilliseconds);
        mood.setCreatedDate(date);
        mood.setRating(cursor.getInt(RATING_INDEX));
        mood.setDescription(cursor.getString(DESCRIPTION_INDEX));

        return mood;
    }
}