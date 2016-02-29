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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ratik.unsplashify.Constants;
import com.ratik.unsplashify.NotificationReceiver;
import com.ratik.unsplashify.R;
import com.ratik.unsplashify.utils.FileUtils;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int WALLPAPER_NOTIF_PENDING_INTENT_ID = 1;

    int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (!intent.hasExtra("from_intent")) {
            setContentView(R.layout.activity_main);

            saveScreenSize();

            Button test = (Button) findViewById(R.id.testButton);
            test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNotification();
                }
            });
        } else {
            setContentView(R.layout.activity_main_show);

            ImageView image = (ImageView) findViewById(R.id.wallpaper);
            Bitmap wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
            image.setImageBitmap(wallpaper);
        }
    }

    private void setNotification() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                WALLPAPER_NOTIF_PENDING_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
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
