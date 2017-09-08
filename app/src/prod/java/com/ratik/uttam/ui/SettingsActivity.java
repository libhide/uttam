package com.ratik.uttam.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ratik.uttam.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ratik on 07/03/16.
 */
public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.settings_content)
    FrameLayout settingsContentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        // Toast for testing
        // Toast.makeText(SettingsActivity.this, "User is " + (MainActivity.userHasRemovedAds ? "PREMIUM" : "NOT PREMIUM"), Toast.LENGTH_SHORT).show();

        // Add Toolbar
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager().beginTransaction().replace(
                R.id.settings_content, new SettingsFragment()).commit();

        // Init Banner Ad
        if (!MainActivity.userHasRemovedAds) {
            // Apply padding to the settings content container
            settingsContentLayout.setPadding(0, 0, 0, 140);
            // Show Ad
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else {
            // Remove margin from the settings content container
            settingsContentLayout.setPadding(0, 0, 0, 0);
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
        finish();
    }
}
