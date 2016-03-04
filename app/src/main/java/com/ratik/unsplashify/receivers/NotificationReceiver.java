package com.ratik.unsplashify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ratik.unsplashify.services.GetPhotoService;

/**
 * Created by Ratik on 29/02/16.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the get-photo service
        context.startService(new Intent(context, GetPhotoService.class));
    }
}
