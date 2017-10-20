package com.ratik.uttam.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.receivers.JobSetReceiver;

import java.util.Calendar;

/**
 * Created by Ratik on 29/03/16.
 */
public class AlarmUtils {
    private static final int JOB_SET_INTENT_ID = 0;

    public static void setJobSetAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(context, JobSetReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                JOB_SET_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        // We do this because on the first day, the user sees
        // the hero wallpaper
        int currentDay = calendar.get(Calendar.DATE);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        if (hours >= 0 && hours < 7) {
            calendar.set(Calendar.DATE, currentDay);
        } else {
            calendar.set(Calendar.DATE, currentDay + 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }
}