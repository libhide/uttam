package com.ratik.uttam.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

        FrameLayout settingsContentLayout = (FrameLayout)
                findViewById(R.id.settings_content);

        // Toast for testing
        Toast.makeText(SettingsActivity.this, "User is " + (MainActivity.userHasRemovedAds ?
                "PREMIUM" : "NOT PREMIUM"), Toast.LENGTH_SHORT).show();

        // Add Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager().beginTransaction().add(
                R.id.settings_content, new SettingsFragment()).commit();

        // Init Banner Ad
        if (!MainActivity.userHasRemovedAds) {
            // Apply margin to settings content container
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 50);
            settingsContentLayout.setLayoutParams(params);
            // Show Ad
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else {
            // Remove margin from the settings content container
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 0);
            settingsContentLayout.setLayoutParams(params);
        }
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
