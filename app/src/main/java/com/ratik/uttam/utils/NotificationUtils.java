package com.ratik.uttam.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.ui.main.MainActivity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 08/09/17.
 */

public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getSimpleName();

    private static final int SHOW_WALLPAPER = 1;
    private static int WALLPAPER_NOTIF_ID = 001;

    private Context context;

    @Inject
    DataStore dataStore;

    public NotificationUtils(Context context) {
        this.context = context;
        Injector.getAppComponent().inject(this);

    }

    public void pushNewWallpaperNotification() {
        dataStore.getPhoto()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        photo -> pushNewNotification(photo),
                        throwable -> pushErrorNotification()
                );
    }

    private void pushNewNotification(Photo photo) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(WALLPAPER_NOTIF_ID, getWallpaperNotification(photo).build());
    }

    private void pushErrorNotification() {
        Log.e(TAG, "Error getting Photo from the DataStore. Notification FAILED.");
    }

    private NotificationCompat.Builder getWallpaperNotification(Photo photo) {
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
