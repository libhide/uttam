package com.ratik.uttam.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.ui.main.MainActivity;

import javax.inject.Inject;

/**
 * Created by Ratik on 08/09/17.
 */

public class NotificationHelper {

    private static final String TAG = NotificationHelper.class.getSimpleName();

    private static final int SHOW_WALLPAPER = 1;
    private static int WALLPAPER_NOTIF_ID = 001;

    private Context context;
    private NotificationManager notificationManager;

    @Inject
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void pushNewNotification(Photo photo) {
        notificationManager.notify(WALLPAPER_NOTIF_ID, getWallpaperNotification(photo).build());
    }

    public void pushErrorNotification(Throwable t) {
        Log.e(TAG, t.getMessage());
    }

    private NotificationCompat.Builder getWallpaperNotification(Photo photo) {
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent showWallpaperIntent = PendingIntent.getActivity(context,
                SHOW_WALLPAPER, intent, 0);

        // Notif Channel for O
        String channelId = Constants.NOTIF_CHANNEL_ID;
        CharSequence channelName = Constants.NOTIF_CHANNEL_NAME;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel
                    = new NotificationChannel(channelId, channelName, importance);

            notificationChannel.setShowBadge(false);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        // TODO: Use Rx to do this work in a background thread (?)
        Bitmap bigBitmap = BitmapFactory.decodeFile(photo.getRegularPhotoUri());
        Bitmap thumbBitmap = BitmapFactory.decodeFile(photo.getThumbPhotoUri());

        return new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_stat_uttam)
                .setLargeIcon(thumbBitmap)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.wallpaper_notif_title))
                .setContentText(context.getString(R.string.wallpaper_notif_photo_by) + photo.getPhotographerName())
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bigBitmap)
                        .setBigContentTitle(context.getString(R.string.wallpaper_notif_title)))
                .setContentIntent(showWallpaperIntent);
    }
}
