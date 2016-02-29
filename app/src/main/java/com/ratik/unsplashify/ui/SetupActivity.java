package com.ratik.unsplashify.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ratik.unsplashify.R;
import com.ratik.unsplashify.utils.Utils;

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
            final EditText intervalEditText = (EditText) findViewById(R.id.intervalEditText);
            Button goButton = (Button) findViewById(R.id.goButton);

            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = intervalEditText.getText().toString();
                    if (!value.isEmpty()) {
                        int interval = Integer.parseInt(value);
                        Utils.setRefreshInterval(SetupActivity.this, interval);
                        startActivity(new Intent(SetupActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(SetupActivity.this, "Please enter a value",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Utils.setFirstRun(this, false);

        } else {
            // Start Main
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
        }
    }
}
