package com.ratik.uttam.ui;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ratik.uttam.R;
import com.ratik.uttam.receivers.NotificationReceiver;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.Utils;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int WALLPAPER_NOTIF_PENDING_INTENT_ID = 1;
    public static final int WALLPAPER_DEFERRED_NOTIF_PENDING_INTENT_ID = 2;
    private static final int FIRST_RUN_NOTIFICATION = 0;
    private static final int SHOW_WALLPAPER = 1;

    private int screenWidth;
    private Bitmap wallpaper;
    private boolean firstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstRun = Utils.isFirstRun(this);

        // Save the screen width for later use
        saveScreenSize();

        if (firstRun) {
            // save hero into the internal storage
            wallpaper = BitmapFactory.decodeResource(getResources(), R.drawable.uttam_hero);
            wallpaper = BitmapUtils.cropBitmapFromCenterAndScreenSize(this, wallpaper);
            FileUtils.saveImage(this, wallpaper, "wallpaper", "png");

            // TODO: refactor
            PhotoUtils.setFullUrl(this, "https://images.unsplash.com/photo-1449024540548-94f5d5a59230?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&s=dec4b59ca06926527007bd98670f2800");
            PhotoUtils.setRegularUrl(this, "https://images.unsplash.com/photo-1449024540548-94f5d5a59230?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&s=dec4b59ca06926527007bd98670f2800");
            PhotoUtils.setPhotographerName(this, "Mike Wilson");

            // set it as the wallpaper
            try {
                WallpaperManager.getInstance(this).setBitmap(wallpaper);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // set alarm
            setNotification();

            // cast first notif
            sendFirstRunNotification();

            // update first run state
            Utils.setFirstRun(this, false);
        } else {
            // get saved image
            wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        }

        // Get photo data
        final String photoUrlFull = PhotoUtils.getFullUrl(this);
        final String photoUrlRegular = PhotoUtils.getRegularUrl(this);
        String photographer = PhotoUtils.getPhotographerName(this);

        // Set ImageView
        ImageView image = (ImageView) findViewById(R.id.wallpaper);
        final Bitmap wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        image.setImageBitmap(wallpaper);

        // Setup Textviews
        TextView photographerTextView = (TextView) findViewById(R.id.photographerTextView);
        photographerTextView.setText(photographer);

        final Intent viewInBrowserIntent = new Intent(Intent.ACTION_VIEW);
        Button viewButton = (Button) findViewById(R.id.viewButton);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoUrlFull.isEmpty() || photoUrlRegular.isEmpty()) {
                    // TODO: handle error
                } else {
                    if (screenWidth > 720) {
                        viewInBrowserIntent.setData(Uri.parse(photoUrlFull));
                    } else {
                        viewInBrowserIntent.setData(Uri.parse(photoUrlRegular));
                    }
                    startActivity(viewInBrowserIntent);
                }
            }
        });

        // Settings
        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setVisibility(View.VISIBLE);

        // Set Wallpaper Button
        ImageButton setWallpaperButton = (ImageButton) findViewById(R.id.wallpaperSetButton);
        setWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set wallpaper
                try {
                    WallpaperManager.getInstance(MainActivity.this).setBitmap(wallpaper);
                } catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });
    }

    private void setNotification() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                WALLPAPER_NOTIF_PENDING_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Utils.getRefreshInterval(this).equals("daily")) {
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
        Utils.setAlarmState(this, true);
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

    private void saveScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        Utils.setScreenWidth(this, screenWidth);
    }

    private void sendFirstRunNotification() {
        // Content Intent
        Intent intent = new Intent(this, ShowActivity.class);

        // Content PendingIntent
        PendingIntent showWallpaperIntent = PendingIntent.getActivity(this,
                SHOW_WALLPAPER, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(wallpaper)
                        .setAutoCancel(true)
                        .setContentTitle("New Wallpaper!")
                        .setContentText("Photo by " + "Mike Wilson")
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(wallpaper)
                                .setBigContentTitle("New Wallpaper!"))
                        .setContentIntent(showWallpaperIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(FIRST_RUN_NOTIFICATION, mBuilder.build());
    }
}
