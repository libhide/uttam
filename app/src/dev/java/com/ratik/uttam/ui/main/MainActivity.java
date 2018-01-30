package com.ratik.uttam.ui.main;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.services.GetPhotoService;
import com.ratik.uttam.ui.settings.SettingsActivity;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.PhotoSaver;
import com.ratik.uttam.utils.Utils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ratik.uttam.R.id.creditsContainer;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    // Constants
    private static final int REQUEST_CODE_SET_WALLPAPER = 1;

    // Member variables
    @Inject
    MainContract.Presenter presenter;

    @Inject
    NotificationUtils notificationUtils;

    @Inject
    PhotoSaver photoSaver;

    private RxPermissions rxPermissions;
    private Photo photo;

    // Views
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Injector.getAppComponent().inject(this);
        ButterKnife.bind(this);

        init();

        // Set the presenter's view
        presenter.setView(this);

        presenter.getPhoto();
    }

    // region INITIALIZATION

    private void init() {
        // Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setIcon(R.drawable.uttam);
        }

        // Adjust theme
        // Do cool stuff for L+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            setTheme(R.style.AppTheme_Fullscreen);
        }

        // Click listeners
        setupClickListeners();
    }

    private void setupClickListeners() {
        rxPermissions = new RxPermissions(this);

        RxView.clicks(saveWallpaperButton)
                .compose(rxPermissions.ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(granted -> {
                    if (granted) {
                        Toast.makeText(MainActivity.this, R.string.wallpaper_saving_message, Toast.LENGTH_SHORT).show();
                        saveWallpaperToExternalStorage();
                        Toast.makeText(MainActivity.this, R.string.wallpaper_saved_message, Toast.LENGTH_SHORT).show();
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(photoSaver.getPhotoFile())));
                    } else {
                        Toast.makeText(this, "Fine, okay. :(", Toast.LENGTH_SHORT).show();
                    }
                });

        RxView.clicks(setWallpaperButton)
                .compose(rxPermissions.ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(granted -> {
                    if (granted) {
                        doWallpaperSetting();
                    } else {
                        Toast.makeText(this, "Fine, okay. :(", Toast.LENGTH_SHORT).show();
                    }
                });

        RxView.clicks(creditsView)
                .subscribe(click -> showWallpaperCredits());
    }

    // endregion

    // region VIEW-LOGIC

    @Override
    public void showPhoto(Photo p) {
        // save the returned photo
        this.photo = p;

        // set views
        wallpaperImageView.setImageBitmap(photo.getPhoto());
        photographerTextView.setText(photo.getPhotographerName());

        if (Utils.isFirstRun(this)) {
            // Set it as the wallpaper
            try {
                WallpaperManager.getInstance(this).setBitmap(photo.getPhoto());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // cast first notification
            notificationUtils.pushNewWallpaperNotification();

            // update first run state
            Utils.setFirstRun(this, false);
        }
    }

    @Override
    public void onGetPhotoFailed() {
        // TODO: use a better error message
        Toast.makeText(this, getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region ACTIVITY OVERRIDES

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SET_WALLPAPER) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.wallpaper_set_text, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                photoSaver.deleteFile();
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
                refreshPhoto();
                return true;
            default:
                return false;
        }
    }

    // endregion

    // region HELPERS

    public void refreshPhoto() {
        startService(new Intent(this, GetPhotoService.class));
        finish();
    }

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

    private void doWallpaperSetting() {
        File savedFile = saveWallpaperToExternalStorage();
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", savedFile);
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
        startActivity(shareIntent);
    }

    private File saveWallpaperToExternalStorage() {
        Date now = new Date();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
        if (!photoSaver.isExternal()) {
            photoSaver.setExternal(true)
                    .setFileName(String.format("wallpaper_%s.png", timestamp))
                    .save(photo.getPhoto());
        }
        return photoSaver.getPhotoFile();
    }

    // endregion
}
