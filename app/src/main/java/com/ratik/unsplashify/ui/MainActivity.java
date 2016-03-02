package com.ratik.unsplashify.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ratik.unsplashify.R;
import com.ratik.unsplashify.receivers.NotificationReceiver;
import com.ratik.unsplashify.utils.FileUtils;
import com.ratik.unsplashify.utils.PhotoUtils;
import com.ratik.unsplashify.utils.Utils;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int WALLPAPER_NOTIF_PENDING_INTENT_ID = 1;

    private int screenWidth;
    private Bitmap wallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Save the screen width for later use
        saveScreenSize();

        wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        if (wallpaper == null) {
            setContentView(R.layout.activity_main);
            if (!Utils.isAlarmSet(this)) {
                // Set the notification
                setNotification();
            } else {
                // TODO: Show countdown to wallpaper fetch
            }
        } else {
            setContentView(R.layout.activity_main_show);

            // Get photo data
            final String photoUrlFull = PhotoUtils.getFullUrl(this);
            final String photoUrlRegular = PhotoUtils.getRegularUrl(this);
            String photographer = PhotoUtils.getPhotographerName(this);

            // Set ImageView
            ImageView image = (ImageView) findViewById(R.id.wallpaper);
            final Bitmap wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
            image.setImageBitmap(wallpaper);

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
            Button settingsButton = (Button) findViewById(R.id.settingsButton);
            settingsButton.setVisibility(View.VISIBLE);

            // Set Wallpaper Button
            Button setWallpaperButton = (Button) findViewById(R.id.wallpaperSetButton);
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
    }

    private void setNotification() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                WALLPAPER_NOTIF_PENDING_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Utils.getRefreshInterval(this).equals("daily")) {
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }

        // Saving alarm-set state
        Utils.setAlarmState(this, true);

        // Finish Activity
        finish();
    }

    private void saveScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        Utils.setScreenWidth(this, screenWidth);
    }
}
