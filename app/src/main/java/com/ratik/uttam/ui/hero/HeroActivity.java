package com.ratik.uttam.ui.hero;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.ratik.uttam.R;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model.PhotoType;
import com.ratik.uttam.ui.main.MainActivity;
import com.ratik.uttam.ui.tour.TourActivity;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FetchUtils;
import com.ratik.uttam.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 06/03/16.
 */
public class HeroActivity extends AppCompatActivity {

    private static final String TAG = HeroActivity.class.getSimpleName();

    @Inject
    DataStore dataStore;

    @Nullable
    @BindView(R.id.uttam)
    ImageView uttamLogo;

    @Nullable
    @BindView(R.id.getStartedButton)
    Button getStartedButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.getAppComponent().inject(this);

        boolean firstRun = Utils.isFirstRun(this);
        if (firstRun) {
            setContentView(R.layout.activity_hero);
            ButterKnife.bind(this);
            doAnimations();
            setupAppForUser();
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

    private void setupAppForUser() {
        // save the user's screen size for later use
        saveScreenSize();

        // set default prefs for the user
        setupDefaultPrefs();

        Single<String> fullPhotoSingle = Single.fromCallable(() -> {
            Bitmap b = BitmapUtils.getBitmapFromResources(this, R.drawable.uttam_hero);
            return storeImage(b, PhotoType.FULL);
        });

        Single<String> regularPhotoSingle = Single.fromCallable(() -> {
            Bitmap b = BitmapUtils.getBitmapFromResources(this, R.drawable.uttam_hero_regular);
            return storeImage(b, PhotoType.REGULAR);
        });

        Single<String> thumbPhotoSingle = Single.fromCallable(() -> {
            Bitmap b = BitmapUtils.getBitmapFromResources(this, R.drawable.uttam_hero_thumb);
            return storeImage(b, PhotoType.THUMB);
        });

        Single.zip(fullPhotoSingle, regularPhotoSingle, thumbPhotoSingle, this::getHeroPhoto)
                .flatMap(photo -> dataStore.putPhoto(photo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> Log.i(TAG, "First save successful"),
                        throwable -> Log.e(TAG, throwable.getMessage())
                );
    }

    @OnClick(R.id.getStartedButton)
    public void getStarted() {
        // Start Tour
        startActivity(new Intent(HeroActivity.this, TourActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void onFirstSaveFail(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
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

    private void saveScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Utils.setScreenWidth(this, size.x);
        Utils.setScreenHeight(this, size.y);
    }

    private void setupDefaultPrefs() {
        // Check if device has resolution under 720p
//        if (Utils.getScreenWidth(this) <= 720) {
//            PrefUtils.setCompressState(this, true);
//        } else {
//            PrefUtils.setCompressState(this, false);
//        }
        dataStore.setAutoSet(true);
    }

    private String storeImage(Bitmap image, PhotoType photoType) {
        File pictureFile = FetchUtils.createFile(this, photoType);
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return pictureFile.getAbsolutePath();
    }

    private Photo getHeroPhoto(String fullUri, String regularUri, String thumbUri) {
        Photo partialHeroPhoto = FetchUtils.getPartialHeroPhoto();
        partialHeroPhoto.setPhotoUri(fullUri);
        partialHeroPhoto.setRegularPhotoUri(regularUri);
        partialHeroPhoto.setThumbPhotoUri(thumbUri);
        return partialHeroPhoto;
    }
}
