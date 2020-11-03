package com.ratik.uttam.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ratik.uttam.R;
import com.ratik.uttam.di.Injector;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ratik on 07/03/16.
 */
public class SettingsActivity extends AppCompatActivity implements SettingsFragment.Callback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.settings_content)
    FrameLayout settingsContentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        Injector.getAppComponent().inject(this);

        // Add Toolbar
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void startContactTheDevFlow() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ratik96@gmail.com"});
        startActivity(emailIntent);
    }

    @Override
    public void startRateTheAppFlow() {
        Intent i = new Intent();
        i.setData(Uri.parse("market://details?id=com.ratik.uttam.prod"));
        startActivity(i);
    }
}

