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

            // Logo
            uttamLogo.setAlpha(0f);
            ObjectAnimator uttamAlphaAnimator = ObjectAnimator.ofFloat(uttamLogo, View.ALPHA, 0f, 1f);
            uttamAlphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            uttamAlphaAnimator.setDuration(1500);

            // Button
            float startValue = getStartedButton.getTranslationY() + 80;
            float endValue = startValue - 80;

            getStartedButton.setY(startValue);
            getStartedButton.setAlpha(0f);

            ObjectAnimator buttonPositionAnimator = ObjectAnimator.ofFloat(getStartedButton, View.TRANSLATION_Y, startValue, endValue);
            ObjectAnimator buttonFadeAnimator = ObjectAnimator.ofFloat(getStartedButton, View.ALPHA, 0f, 1f);

            AnimatorSet buttonSet = new AnimatorSet();
            buttonSet.playTogether(buttonPositionAnimator, buttonFadeAnimator);
            buttonSet.setDuration(1000);
            buttonSet.setStartDelay(500);
            buttonSet.setInterpolator(new AccelerateDecelerateInterpolator());

            AnimatorSet set = new AnimatorSet();
            set.playTogether(buttonSet, uttamAlphaAnimator);
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
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
