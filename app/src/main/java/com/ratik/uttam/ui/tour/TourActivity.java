package com.ratik.uttam.ui.tour;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.data.PrefStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.data.model.Photo;
import com.ratik.uttam.data.model.PhotoType;
import com.ratik.uttam.network.FileProvider;
import com.ratik.uttam.ui.main.MainActivity;
import com.ratik.uttam.util.AlarmHelper;
import com.ratik.uttam.util.BitmapHelper;
import com.vlonjatg.android.apptourlibrary.AppTour;
import com.vlonjatg.android.apptourlibrary.MaterialSlide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 08/03/16.
 */
public class TourActivity extends AppTour {

    public static final String TAG = TourActivity.class.getSimpleName();

    @Inject
    PhotoStore photoStore;

    @Inject
    PrefStore prefStore;

    @Inject
    WallpaperManager wallpaperManager;

    @Inject
    FileProvider fileProvider;

    @Inject
    AlarmHelper alarmHelper;

    @Inject
    BitmapHelper bitmapHelper;

    private CompositeDisposable compositeDisposable;

    private AllDoneFragment doneFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getAppComponent().inject(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        int slideColor = Color.parseColor("#3d3d3d");

        String first = getString(R.string.tour_slide_1_text);
        String second = getString(R.string.tour_slide_2_text);
        String third = getString(R.string.tour_slide_3_text);

        Fragment firstSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_1,
                getString(R.string.tour_slide_1_heading), first, Color.WHITE, Color.WHITE);

        Fragment secondSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_2,
                getString(R.string.tour_slide_2_heading), second, Color.WHITE, Color.WHITE);

        Fragment thirdSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_3,
                getString(R.string.tour_slide_3_heading), third, Color.WHITE, Color.WHITE);

        addSlide(firstSlide, slideColor);
        addSlide(secondSlide, slideColor);
        addSlide(thirdSlide, slideColor);
        doneFragment = new AllDoneFragment();
        addSlide(doneFragment, slideColor);

        // Customize tour
        hideSkip();
        setNextButtonColorToWhite();
        setDoneButtonTextColor(Color.WHITE);
        setActiveDotColor(Color.BLACK);
    }

    @Override
    public void onSkipPressed() {
        setCurrentSlide(4);
    }

    @Override
    public void onDonePressed() {
        setupAppForUser();
        doneFragment.showProgressBar();
    }

    private void setupAppForUser() {
        // set alarm to set job for 7 AM
        alarmHelper.setJobSetAlarm();

        setupDefaultPrefs();

        Single<String> fullPhotoSingle = Single.fromCallable(() -> {
            Bitmap b = bitmapHelper.getBitmapFromResources(R.drawable.uttam_hero);
            return storeImage(b, PhotoType.FULL);
        });

        Single<String> regularPhotoSingle = Single.fromCallable(() -> {
            Bitmap b = bitmapHelper.getBitmapFromResources(R.drawable.uttam_hero_regular);
            return storeImage(b, PhotoType.REGULAR);
        });

        Single<String> thumbPhotoSingle = Single.fromCallable(() -> {
            Bitmap b = bitmapHelper.getBitmapFromResources(R.drawable.uttam_hero_thumb);
            return storeImage(b, PhotoType.THUMB);
        });

        compositeDisposable.add(
                Single.zip(fullPhotoSingle, regularPhotoSingle, thumbPhotoSingle, this::getHeroPhoto)
                        .flatMapCompletable(photo -> photoStore.putPhoto(photo))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    Log.i(TAG, "App setup done!");
                                    startMain();
                                },
                                throwable -> Log.e(TAG, throwable.getMessage())
                        )
        );
    }

    private void startMain() {
        startActivity(new Intent(TourActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void setupDefaultPrefs() {
        prefStore.enableWallpaperAutoSet();
        prefStore.setDesiredWallpaperWidth(wallpaperManager.getDesiredMinimumWidth());
        prefStore.setDesiredWallpaperHeight(wallpaperManager.getDesiredMinimumHeight());
    }

    private String storeImage(Bitmap image, PhotoType photoType) {
        File pictureFile = fileProvider.createFile(photoType);
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
        Photo partialHeroPhoto = getPartialHeroPhoto();
        partialHeroPhoto.setFullPhotoUri(fullUri);
        partialHeroPhoto.setRegularPhotoUri(regularUri);
        partialHeroPhoto.setThumbPhotoUri(thumbUri);
        return partialHeroPhoto;
    }

    public Photo getPartialHeroPhoto() {
        return new Photo(
                Constants.Fetch.FIRST_WALLPAPER_ID,
                null,
                null,
                null,
                Constants.Fetch.FIRST_WALLPAPER_PHOTOGRAPHER_NAME,
                Constants.Fetch.FIRST_WALLPAPER_PHOTOGRAPHER_USERNAME,
                Constants.Fetch.FIRST_WALLPAPER_FULL_URL,
                Constants.Fetch.FIRST_WALLPAPER_DOWNLOAD_URL,
                null,
                Constants.Fetch.FIRST_WALLPAPER_HTML_URL
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
