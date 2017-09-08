package com.ratik.uttam.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.ui.MainActivity;
import com.ratik.uttam.ui.ShowActivity;

/**
 * Created by Ratik on 08/09/17.
 */

public class NotificationUtils {

    private static final int SHOW_WALLPAPER = 1;
    private static int WALLPAPER_NOTIF_ID = 001;
    private static final int FIRST_RUN_NOTIFICATION = 0;

    public static void pushNewWallpaperNotif(Context context, Photo photo, Bitmap wallpaper) {
        // Content Intent
        Intent intent;
        if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
            intent = new Intent(context, MainActivity.class);
        } else {
            intent = new Intent(context, ShowActivity.class);
        }

        // Content PendingIntent
        PendingIntent showWallpaperIntent = PendingIntent.getActivity(context,
                SHOW_WALLPAPER, intent, 0);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notif Channel for O
        String channelId = Constants.NOTIF_CHANNEL_ID;
        CharSequence channelName = Constants.NOTIF_CHANNEL_NAME;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel
                    = new NotificationChannel(channelId, channelName, importance);

            if (PrefUtils.userWantsCustomSounds(context)) {
                AudioAttributes attrs = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.uttam), attrs);
            }

            if (PrefUtils.userWantsNotificationLED(context)) {
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.WHITE);
            }

            notificationChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_stat_uttam)
                        .setLargeIcon(BitmapUtils.cropToSquare(wallpaper))
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.wallpaper_notif_title))
                        .setContentText(context.getString(R.string.wallpaper_notif_photo_by) + photo.getPhotographer())
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(wallpaper)
                                .setBigContentTitle(context.getString(R.string.wallpaper_notif_title)))
                        .setContentIntent(showWallpaperIntent);

        if (PrefUtils.userWantsCustomSounds(context)) {
            builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.uttam));
        }

        if (PrefUtils.userWantsNotificationLED(context)) {
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
        }

        // Push notification
        notificationManager.notify(WALLPAPER_NOTIF_ID, builder.build());
    }

    public static void pushFirstNotification(Context context, Bitmap wallpaper) {
        // Content Intent
        Intent intent = new Intent(context, MainActivity.class);

        // Content PendingIntent
        PendingIntent showWallpaperIntent = PendingIntent.getActivity(context,
                SHOW_WALLPAPER, intent, 0);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notif Channel for O
        String channelId = Constants.NOTIF_CHANNEL_ID;
        CharSequence channelName = Constants.NOTIF_CHANNEL_NAME;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel
                    = new NotificationChannel(channelId, channelName, importance);

            if (PrefUtils.userWantsCustomSounds(context)) {
                AudioAttributes attrs = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.uttam), attrs);
            }

            if (PrefUtils.userWantsNotificationLED(context)) {
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.WHITE);
            }

            notificationChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_stat_uttam)
                        .setLargeIcon(BitmapUtils.cropToSquare(wallpaper))
                        .setAutoCancel(true)
                        .setContentTitle("New Wallpaper!")
                        .setContentText("Photo by " + "Martin Sanchez")
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(wallpaper)
                                .setBigContentTitle("New Wallpaper!"))
                        .setContentIntent(showWallpaperIntent);

        if (PrefUtils.userWantsCustomSounds(context)) {
            builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.uttam));
        }

        if (PrefUtils.userWantsNotificationLED(context)) {
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
        }

        notificationManager.notify(FIRST_RUN_NOTIFICATION, builder.build());
    }
}
