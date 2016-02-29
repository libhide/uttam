package com.ratik.unsplashify.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import com.ratik.unsplashify.Constants;
import com.ratik.unsplashify.R;
import com.ratik.unsplashify.receivers.NotificationReceiver;
import com.ratik.unsplashify.utils.FileUtils;
import com.ratik.unsplashify.utils.Utils;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int WALLPAPER_NOTIF_PENDING_INTENT_ID = 1;

    private int screenWidth;
    private Bitmap wallpaper;
    private int refreshInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Save the screen width for later use
        saveScreenSize();

        // Retrieve refresh interval
        refreshInterval = Utils.getRefreshInterval(this);

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
            ImageView image = (ImageView) findViewById(R.id.wallpaper);
            Bitmap wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
            image.setImageBitmap(wallpaper);
        }
    }

    private void setNotification() {
        Toast.makeText(this, "Setting fetch alarm", Toast.LENGTH_LONG).show();
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, minutes + refreshInterval);

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                WALLPAPER_NOTIF_PENDING_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                refreshInterval * 60 * 1000, pendingIntent);

        // Saving alarm-set state
        Utils.setAlarmState(this, true);
    }

    private void saveScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.SCREEN_WIDTH, screenWidth);
        editor.apply();
    }
}
