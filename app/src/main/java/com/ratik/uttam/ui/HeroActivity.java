package com.ratik.uttam.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.ratik.uttam.R;
import com.ratik.uttam.utils.Utils;

/**
 * Created by Ratik on 06/03/16.
 */
public class HeroActivity extends AppCompatActivity {

    /*
     This activity will eventually act as the hero page which will further
     lead to an app tutorial and setup.
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean firstRun = Utils.isFirstRun(this);
        if (firstRun) {
            setContentView(R.layout.activity_hero);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Refresh Interval");

            String[] intervals = getResources().getStringArray(R.array.intervals);
            ArrayAdapter<String> intervalsAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_single_choice, intervals);
            builder.setAdapter(intervalsAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            // DAILY
                            Utils.setRefreshInterval(HeroActivity.this, "daily");
                            break;
                        case 1:
                            // WEEKLY
                            Utils.setRefreshInterval(HeroActivity.this, "weekly");
                            break;
                    }
                    startActivity(new Intent(HeroActivity.this, MainActivity.class));
                    // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            });

            Button getStartedButton = (Button) findViewById(R.id.getStartedButton);
            getStartedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Make app tour
                    // For now, we just function like SetupActivity
                    builder.create().show();
                }
            });
        } else {
            // Start Main
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);

            finish();
        }
    }
}
