package com.ratik.uttam.ui.hero;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ratik.uttam.R;
import com.ratik.uttam.data.PrefStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.ui.main.MainActivity;
import com.ratik.uttam.ui.tour.TourActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ratik on 06/03/16.
 */
public class HeroActivity extends AppCompatActivity {

    @Inject
    PrefStore prefStore;

    @BindView(R.id.uttam)
    ImageView uttamLogo;

    @BindView(R.id.getStartedButton)
    Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.getAppComponent().inject(this);

        if (prefStore.isFirstRun()) {
            setContentView(R.layout.activity_hero);
            ButterKnife.bind(this);
            doAnimations();
        } else {
            setContentView(R.layout.activity_splash);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setExitTransition(new Fade());
            }

            new Handler().postDelayed(() -> {
                // Start Main
                startActivity(new Intent(HeroActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, 600);
        }
    }

    @OnClick(R.id.getStartedButton)
    public void getStarted() {
        // Start Tour
        startActivity(new Intent(HeroActivity.this, TourActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void doAnimations() {
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

        ObjectAnimator buttonPositionAnimator = ObjectAnimator.ofFloat(getStartedButton,
            View.TRANSLATION_Y, startValue, endValue);
        ObjectAnimator buttonFadeAnimator = ObjectAnimator.ofFloat(getStartedButton,
            View.ALPHA, 0f, 1f);

        AnimatorSet buttonSet = new AnimatorSet();
        buttonSet.playTogether(buttonPositionAnimator, buttonFadeAnimator);
        buttonSet.setDuration(1000);
        buttonSet.setStartDelay(500);
        buttonSet.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(buttonSet, uttamAlphaAnimator);
        set.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
