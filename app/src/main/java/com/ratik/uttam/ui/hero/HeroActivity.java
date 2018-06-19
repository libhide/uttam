package com.ratik.uttam.ui.hero;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.ratik.uttam.R;
import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.data.PrefStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model.PhotoType;
import com.ratik.uttam.ui.main.MainActivity;
import com.ratik.uttam.ui.tour.TourActivity;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FetchUtils;

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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 06/03/16.
 */
public class HeroActivity extends AppCompatActivity {

    private static final String TAG = HeroActivity.class.getSimpleName();

    @Inject
    PhotoStore photoStore;

    @Inject
    PrefStore prefStore;

    private CompositeDisposable compositeDisposable;

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

        compositeDisposable = new CompositeDisposable();

        if (prefStore.isFirstRun()) {
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

        compositeDisposable.add(
                Single.zip(fullPhotoSingle, regularPhotoSingle, thumbPhotoSingle, this::getHeroPhoto)
                        .flatMapCompletable(photo -> photoStore.putPhoto(photo))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> Log.i(TAG, "First save successful"),
                                throwable -> Log.e(TAG, throwable.getMessage())
                        )
        );
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void setupDefaultPrefs() {
        prefStore.enableWallpaperAutoSet();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        prefStore.setDesiredWallpaperWidth(wallpaperManager.getDesiredMinimumWidth());
        prefStore.setDesiredWallpaperHeight(wallpaperManager.getDesiredMinimumHeight());
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
