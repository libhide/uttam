package com.ratik.uttam.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.receivers.NotificationReceiver;

import java.util.Calendar;

/**
 * Created by Ratik on 29/03/16.
 */
public class AlarmHelper {
    public static final int WALLPAPER_NOTIF_PENDING_INTENT_ID = 1;
    public static final int WALLPAPER_DEFERRED_NOTIF_PENDING_INTENT_ID = 2;

    public static void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                WALLPAPER_NOTIF_PENDING_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (PrefUtils.getRefreshInterval(context).equals("daily")) {
            // We do this because on the first day, the user sees
            // the uttam hero wallpaper
            int currentDay = calendar.get(Calendar.DATE);
            calendar.set(Calendar.DATE, currentDay + 1);
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            int currentDay = calendar.get(Calendar.DATE);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }

        // Saving alarm-set state
        Utils.setAlarmState(context, true);
    }

    public static void setAlarmPostReboot(Context context) {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                WALLPAPER_NOTIF_PENDING_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (PrefUtils.getRefreshInterval(context).equals("daily")) {
            if (calendar.get(Calendar.HOUR_OF_DAY) > 7) {
                int currentDay = calendar.get(Calendar.DATE);
                calendar.set(Calendar.DATE, currentDay + 1);
            }

            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }

        // Saving alarm-set state
        Utils.setAlarmState(context, true);
    }

    public static void postponeAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                WALLPAPER_DEFERRED_NOTIF_PENDING_INTENT_ID, intent, 0);

        // Postpone fetch by one hour
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, currentHour + 1);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

        // Saving alarm-set state
        Utils.setAlarmState(context, true);
    }

}
