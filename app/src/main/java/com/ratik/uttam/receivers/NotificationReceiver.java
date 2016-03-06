package com.ratik.uttam.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.services.GetPhotoService;
import com.ratik.uttam.ui.MainActivity;
import com.ratik.uttam.utils.NetworkUtils;

import java.util.Calendar;

/**
 * Created by Ratik on 29/02/16.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // TODO: have a check for Wifi or Data
        if (NetworkUtils.isNetworkAvailable(context)) {
            // Start the get-photo service
            context.startService(new Intent(context, GetPhotoService.class));
        } else {
            setDeferredNotification();
        }
    }

    private void setDeferredNotification() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                MainActivity.WALLPAPER_DEFERRED_NOTIF_PENDING_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Defer by an hour
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.MINUTE,  + currentHour + 1);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }
}
