package com.hendon.moodsy.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.hendon.moodsy.LogMoodActivity;
import com.hendon.moodsy.R;

import java.util.Calendar;

public class Alarm extends WakefulBroadcastReceiver {
    // Used when null reminderTime used in setAlarm.
    final private int DEFAULT_REMINDER_HOUR_OF_DAY = 15; // 3pm

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, LogMoodActivity.class);

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
    }

    public void setAlarm(Context context, Calendar reminderTime) {
        this.context = context;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 1:30 p.m.
        if (reminderTime == null) {
            calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_REMINDER_HOUR_OF_DAY);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, reminderTime.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, reminderTime.get(Calendar.MINUTE));
        }

        // Set the alarm to fire at approximately 8:30 a.m., according to the device's
        // clock, and to repeat once a day.
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void notifyTimeToCheckIn() {
        if (context == null) return;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_moodsy_notification)
                        .setContentTitle("Checking in")
                        .setContentText("How are you feeling?");

        Intent resultIntent = new Intent(context, LogMoodActivity.class);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        // TODO: Make device vibrate when notifying user.

    }
}