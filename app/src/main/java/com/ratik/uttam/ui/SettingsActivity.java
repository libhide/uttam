package com.ratik.uttam.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ratik.uttam.R;
import com.ratik.uttam.receivers.NotificationReceiver;
import com.ratik.uttam.utils.AlarmHelper;

/**
 * Created by Ratik on 07/03/16.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Add Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager().beginTransaction().add(
                R.id.settings_content, new SettingsFragment()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goToMain();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, AlarmHelper.WALLPAPER_NOTIF_PENDING_INTENT_ID, intent, 0);
        AlarmManager oldAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        oldAlarm.cancel(sender);

        // Set new alarm
        AlarmHelper.setAlarm(this);

        // Finish
        finish();
    }
}
