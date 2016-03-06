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
 * Created by Ratik on 29/02/16.
 */
public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean firstRun = Utils.isFirstRun(this);
        if (firstRun) {
            // Setup
            setContentView(R.layout.activity_setup);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Refresh Interval");

            String[] intervals = getResources().getStringArray(R.array.intervals);
            ArrayAdapter<String> intervalsAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, intervals);
            builder.setAdapter(intervalsAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            // DAILY
                            Utils.setRefreshInterval(SetupActivity.this, "daily");
                            break;
                        case 1:
                            // WEEKLY
                            Utils.setRefreshInterval(SetupActivity.this, "weekly");
                            break;
                    }
                    startActivity(new Intent(SetupActivity.this, MainActivity.class));

                    finish();
                }
            });

            Button goButton = (Button) findViewById(R.id.goButton);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.create().show();
                }
            });

            Utils.setFirstRun(this, false);

        } else {
            // Start Main
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);

            finish();
        }
    }
}
