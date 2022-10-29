package com.ratik.uttam.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.receiver.JobSetReceiver;

import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by Ratik on 29/03/16.
 */
public class AlarmHelper {
    private final int JOB_SET_INTENT_ID = 0;
    private final Context context;

    @Inject
    public AlarmHelper(Context context) {
        this.context = context;
    }

    public void setJobSetAlarm() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(context, JobSetReceiver.class);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context,
                    JOB_SET_INTENT_ID, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context,
                    JOB_SET_INTENT_ID, intent, 0);
        }

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        // We do this because on the first day the
        // user sees the hero wallpaper
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

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
