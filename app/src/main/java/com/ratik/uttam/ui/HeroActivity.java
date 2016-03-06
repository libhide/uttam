package com.ratik.uttam.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ratik.uttam.R;
import com.ratik.uttam.utils.Utils;

/**
 * Created by Ratik on 06/03/16.
 */
public class HeroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean firstRun = Utils.isFirstRun(this);
        if (firstRun) {
            setContentView(R.layout.activity_hero);

            Button getStartedButton = (Button) findViewById(R.id.getStartedButton);
            getStartedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Start tutorial
                }
            });
        }
//        } else {
//            // Start Main
//            startActivity(new Intent(this, MainActivity.class));
//            overridePendingTransition(0, 0);
//
//            finish();
//        }
    }
}
