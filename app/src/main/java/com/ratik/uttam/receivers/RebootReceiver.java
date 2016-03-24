package com.ratik.uttam.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.ui.MainActivity;
import com.ratik.uttam.utils.Utils;

import java.util.Calendar;

/**
 * Created by Ratik on 02/03/16.
 */
public class RebootReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (Utils.isAlarmSet(context)) {
            // Set alarm again
            setNotification();
        }
    }

    private void setNotification() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                MainActivity.WALLPAPER_NOTIF_PENDING_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Utils.getRefreshInterval(context).equals("daily")) {
            // Setting calendar to next day, 7 AM
            if (itIsBeforeSeven(calendar)) {
                // nothing
            } else {
                int currentDay = calendar.get(Calendar.DATE);
                calendar.set(Calendar.DATE, currentDay + 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            int currentDay = calendar.get(Calendar.DATE);
            calendar.set(Calendar.DATE, currentDay + 1);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }

        // Saving alarm-set state
        Utils.setAlarmState(context, true);
    }

    // TODO: refactor name
    private boolean itIsBeforeSeven(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 7) {
            return false;
        } else {
            return true;
        }
    }
}
