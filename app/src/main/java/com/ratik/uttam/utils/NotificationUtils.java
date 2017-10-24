package com.ratik.uttam.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.ui.main.MainActivity;

/**
 * Created by Ratik on 08/09/17.
 */

public class NotificationUtils {

    private static final int SHOW_WALLPAPER = 1;
    private static int WALLPAPER_NOTIF_ID = 001;
    private static final int FIRST_RUN_NOTIFICATION = 0;

    public static void pushNewWallpaperNotif(Context context, Photo photo) {
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

            notificationChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Bitmap wallpaper = FileUtils.getBitmapFromInternalStorage(context, photo.getPhotoFileName());
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_stat_uttam)
                        .setLargeIcon(BitmapUtils.cropToSquare(wallpaper))
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.wallpaper_notif_title))
                        .setContentText(context.getString(R.string.wallpaper_notif_photo_by) + photo.getPhotographerName())
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(wallpaper)
                                .setBigContentTitle(context.getString(R.string.wallpaper_notif_title)))
                        .setContentIntent(showWallpaperIntent);

        // Push notification
        notificationManager.notify(WALLPAPER_NOTIF_ID, builder.build());
    }

    public static void pushFirstNotification(Context context, Photo photo) {
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

            notificationChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Bitmap wallpaper = FileUtils.getBitmapFromInternalStorage(context, photo.getPhotoFileName());
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_stat_uttam)
                        .setLargeIcon(BitmapUtils.cropToSquare(wallpaper))
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.wallpaper_notif_title))
                        .setContentText(context.getString(R.string.wallpaper_notif_photo_by) + photo.getPhotographerName())
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(wallpaper)
                                .setBigContentTitle(context.getString(R.string.wallpaper_notif_title)))
                        .setContentIntent(showWallpaperIntent);

        notificationManager.notify(FIRST_RUN_NOTIFICATION, builder.build());
    }
}
