package com.ratik.uttam.ui.main;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jakewharton.rxbinding2.view.RxView;
import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.billing.BillingManager;
import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.data.PrefStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.ui.settings.SettingsActivity;
import com.ratik.uttam.util.BitmapHelper;
import com.ratik.uttam.util.FileHelper;
import com.ratik.uttam.util.NotificationHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.ratik.uttam.R.id.creditsContainer;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_SET_WALLPAPER = 1;

    @Inject
    MainContract.Presenter presenter;

    @Inject
    NotificationHelper notificationHelper;

    @Inject
    PrefStore prefStore;

    @Inject
    PhotoStore photoStore;

    @Inject
    BitmapHelper bitmapHelper;

    private FileHelper fileHelper;
    private CompositeDisposable compositeDisposable;
    private RxPermissions rxPermissions;
    private Photo photo;

    @Inject
    Billing billing;
    private boolean addFree = true;
    private BillingManager billingManager;

    private InterstitialAd savingAd;

    @BindView(R.id.wallpaperScrollView)
    HorizontalScrollView wallpaperScrollView;

    @BindView(R.id.wallpaper)
    ImageView wallpaperImageView;

    @BindView(R.id.photographerTextView)
    TextView photographerTextView;

    @BindView(R.id.wallpaperSetButton)
    ImageButton setWallpaperButton;

    @BindView(creditsContainer)
    LinearLayout creditsView;

    @BindView(R.id.wallpaperSaveButton)
    ImageButton saveWallpaperButton;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.refetchOverlay)
    View refetchOverlay;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Injector.getAppComponent().inject(this);

        compositeDisposable = new CompositeDisposable();
        fileHelper = new FileHelper();

        init();

        presenter.setView(this);
        presenter.getPhoto();
    }

    // region INITIALIZATION

    private void init() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setIcon(R.drawable.uttam);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.END));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            setTheme(R.style.AppTheme_Fullscreen);
        }

        setupClickListeners();
        setupAds();

        // Billing
        billingManager = new BillingManager(this, billing);
        billingManager.loadInventory(products -> {
            final Inventory.Product product = products.get(ProductTypes.IN_APP);
            if (product.isPurchased(Constants.Billing.SKU_REMOVE_ADS)) {
                addFree = true;
                return;
            }
            addFree = false;
        });
    }

    private void setupClickListeners() {
        rxPermissions = new RxPermissions(this);

        compositeDisposable.add(
                RxView.clicks(saveWallpaperButton)
                        .compose(rxPermissions.ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        .subscribe(granted -> {
                            if (granted) {
                                saveWallpaper();
                            } else {
                                Toast.makeText(this, "Fine, okay. :(", Toast.LENGTH_SHORT).show();
                            }
                        })
        );

        compositeDisposable.add(
                RxView.clicks(setWallpaperButton)
                        .compose(rxPermissions.ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        .subscribe(granted -> {
                            if (granted) {
                                doWallpaperSetting();
                            } else {
                                Toast.makeText(this, "Fine, okay. :(", Toast.LENGTH_SHORT).show();
                            }
                        })
        );

        compositeDisposable.add(
                RxView.clicks(creditsView)
                        .subscribe(click -> showWallpaperCredits())
        );
    }

    // endregion

    // region VIEW-LOGIC

    @Override
    public void showPhoto(Photo p) {
        // save the returned photo
        photo = p;

        setWallpaperImageView();
        photographerTextView.setText(photo.getPhotographerName());
    }

    private void setWallpaperImageView() {
        compositeDisposable.add(
                bitmapHelper.getBitmapFromFile(photo.getFullPhotoUri())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onWallpaperImageViewSetSuccess,
                                this::onWallpaperImageViewSetFailure
                        )
        );
    }

    private void onWallpaperImageViewSetFailure(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    private void onWallpaperImageViewSetSuccess(Bitmap bitmap) {
        wallpaperImageView.setImageBitmap(bitmap);

        if (prefStore.isFirstRun()) {
            try {
                WallpaperManager.getInstance(this).setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            pushFirstWallpaperNotification();
        }
    }

    private void pushFirstWallpaperNotification() {
        compositeDisposable.add(
                photoStore.getPhoto()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(photo -> {
                            notificationHelper.pushNewNotification(photo);
                            prefStore.firstRunDone();
                        }, t -> notificationHelper.pushErrorNotification(t))
        );
    }

    @Override
    public void onGetPhotoFailed() {
        Toast.makeText(this, getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRefetchProgress() {
        Toast.makeText(this, "Fetching wallpaper...", Toast.LENGTH_LONG).show();
        refetchOverlay.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRefetchProgress() {
        presenter.getPhoto();
        refetchOverlay.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showRefetchError(String errorMessage) {
        refetchOverlay.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region ACTIVITY OVERRIDES

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SET_WALLPAPER) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.wallpaper_set_text, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_share:
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                shareWallpaper();
                            }
                        });
                return true;
            case R.id.action_refresh:
                presenter.refetchPhoto();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        billingManager.stopCheckout();
        super.onDestroy();
        presenter.detachView();
        compositeDisposable.dispose();
    }

    // endregion

    // region HELPERS

    public void showWallpaperCredits() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.General.BASE_DOMAIN)
                .appendEncodedPath(photo.getPhotographerUserName())
                .appendQueryParameter("utm_source", "uttam")
                .appendQueryParameter("utm_medium", "referral");
        String url = builder.build().toString();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void saveWallpaperToExternalStorage() throws IOException {
        File srcFile = new File(photo.getFullPhotoUri());

        compositeDisposable.add(
                fileHelper.exportFile(srcFile, photo.getId() + ".png")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onSaveSuccess, this::onSaveFailure)
        );
    }

    private void onSaveFailure(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    private void onSaveSuccess(File file) {
        Toast.makeText(MainActivity.this, R.string.wallpaper_saved_message, Toast.LENGTH_SHORT).show();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    private void saveWallpaper() throws IOException {
        if (addFree) {
            saveWallpaperToExternalStorage();
        } else {
            if (savingAd.isLoaded()) {
                savingAd.show();
                Toast.makeText(MainActivity.this,
                        "Close the ad to continue saving...",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doWallpaperSetting() throws IOException {
        File srcFile = new File(photo.getFullPhotoUri());

        compositeDisposable.add(
                fileHelper.exportFile(srcFile, photo.getId() + ".png")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onSettingSaveSuccess, this::onSettingSaveFailure)
        );
    }

    private void onSettingSaveFailure(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    private void onSettingSaveSuccess(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        if (uri != null) {
            Intent intent = WallpaperManager.getInstance(this).getCropAndSetWallpaperIntent(uri);
            startActivityForResult(intent, REQUEST_CODE_SET_WALLPAPER);
        } else {
            Toast.makeText(this, "Uri is null.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareWallpaper() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String shareText = String.format(getString(R.string.wallpaper_share_text),
                photo.getPhotographerName(), photo.getPhotoDownloadUrl());
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    private void setupAds() {
        savingAd = new InterstitialAd(this);
        savingAd.setAdUnitId(Constants.Ads.INTERSTITIAL_AD);
        savingAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial(savingAd);
                try {
                    saveWallpaperToExternalStorage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        requestNewInterstitial(savingAd);
    }

    private void requestNewInterstitial(InterstitialAd ad) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("AE9739935347E14CA9FDC906502E058F")
                .build();

        ad.loadAd(adRequest);
    }

    // endregion
}