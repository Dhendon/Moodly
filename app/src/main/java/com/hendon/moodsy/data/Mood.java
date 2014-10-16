package com.hendon.moodsy.data;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by davidhendon on 9/26/14.
 */
public class Mood {
    private int rating = 0; // Scale from -5 to 5 with negative being bad mood
    private String description;
    private Calendar createdDate;
    private long tableID;

    public Mood() {
        this(0);
    }

    public Mood(int rating) {
        this(rating, "");
    }

    public Mood(int rating, String description) {
        this(rating, description, Calendar.getInstance());
    }

    /**
     * Constructor used for testing purposes.
     *
     * @param rating
     * @param description
     * @param createdDate
     */
    protected Mood(int rating, String description, Calendar createdDate) {
        this.rating = rating;
        this.description = description;
        this.createdDate = createdDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Calendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Calendar createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTableID() {
        return tableID;
    }

    public void setTableID(long tableID) {
        this.tableID = tableID;
    }

    @Override
    public String toString() {
        String dateFormatPattern = "MM/dd/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern, Locale.US);
        String formattedDate = sdf.format(createdDate);
        return "Rating:" + rating + " Created Date:" + formattedDate;
    }
}
