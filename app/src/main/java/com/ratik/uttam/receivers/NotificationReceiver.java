package com.ratik.uttam.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.services.GetPhotoService;
import com.ratik.uttam.utils.PrefUtils;

import java.util.Calendar;

/**
 * Created by Ratik on 29/02/16.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PrefUtils.getRefreshInterval(context).equals("weekly")) {
            // If the refresh is set to weekly
            // Double check to see if the day is Monday
            // before starting the service
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                context.startService(new Intent(context, GetPhotoService.class));
            }
        } else {
            // If the refresh is set to daily,
            // just start the service
            context.startService(new Intent(context, GetPhotoService.class));
        }

    }
}
