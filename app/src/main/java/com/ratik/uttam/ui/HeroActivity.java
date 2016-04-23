package com.ratik.uttam.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

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

            ImageView uttamLogo = (ImageView) findViewById(R.id.uttam);
            Button getStartedButton = (Button) findViewById(R.id.getStartedButton);

            if (getStartedButton != null) {
                getStartedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(HeroActivity.this, TourActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }

            // Animations
            float startVal = uttamLogo.getY() - 200;
            float endVal = uttamLogo.getY() + 220;
            uttamLogo.setY(startVal);

            ObjectAnimator uttamPositionAnimator = ObjectAnimator.ofFloat(uttamLogo, View.Y, startVal, endVal);
            uttamPositionAnimator.setDuration(800);

            ObjectAnimator uttamAlphaAnimator = ObjectAnimator.ofFloat(uttamLogo, View.ALPHA, 0, 1);
            uttamAlphaAnimator.setDuration(1200);

            AnimatorSet logoSet = new AnimatorSet();
            logoSet.setDuration(1200);
            logoSet.setInterpolator(new AccelerateDecelerateInterpolator());
            logoSet.playTogether(uttamAlphaAnimator, uttamPositionAnimator);

            getStartedButton.setAlpha(0);
            ObjectAnimator buttonFadeAnimator = ObjectAnimator.ofFloat(getStartedButton, View.ALPHA, 0, 1);
            buttonFadeAnimator.setDuration(800);
            buttonFadeAnimator.setInterpolator(new AccelerateInterpolator());

            AnimatorSet set = new AnimatorSet();
            set.playTogether(logoSet, buttonFadeAnimator);
            set.start();
        } else {
            setContentView(R.layout.activity_splash);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setExitTransition(new Fade());
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start Main
                    startActivity(new Intent(HeroActivity.this, MainActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }, 1300);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
