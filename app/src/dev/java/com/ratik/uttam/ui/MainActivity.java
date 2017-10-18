package com.ratik.uttam.ui;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.data.DatabaseRealm;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model._Photo;
import com.ratik.uttam.services.GetPhotoService;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.Utils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ratik.uttam.R.id.creditsContainer;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    // Constants
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int WALL_JOB_ID = 1;

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

    // Helpers
    private boolean shouldScroll;
    private File destFile;

    @Inject
    MainContract.Presenter presenter;

    @Inject
    DatabaseRealm realm;

    private Bitmap wallpaper;
    private _Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Injection
        Injector.getAppComponent().inject(this);

        // Bind views
        ButterKnife.bind(this);

        init();

        // set the presenter's view
        presenter.setView(this);

        if (Utils.isFirstRun(this)) {
            // Create first photo
            _Photo photo = new _Photo();
            photo.setPhotographerName("Efe Kurnaz");
            photo.setPhotographerUserName("@efekurnaz");
            photo.setPhotoFullUrl("https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?dpr=1&auto=format&fit=crop&w=1534&h=&q=60&cs=tinysrgb&crop=");
            photo.setPhotoFSPath("wallpaper.png");
            photo.setPhotoHtmlUrl("https://unsplash.com/photos/RnCPiXixooY");
            photo.setPhotoDownloadUrl("https://unsplash.com/photos/RnCPiXixooY/download");

            presenter.setPhoto(photo);

            // save actual photo to internal storage
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.uttam_hero);
            FileUtils.saveImage(this, b, "wallpaper.png");

            presenter.loadPhoto();

            // set alarm to set job for 7am daily
            // AlarmUtils.setJobSetAlarm(this);

            // update first run state
            Utils.setFirstRun(this, false);
        } else {
            // Load the photo from storage
            presenter.loadPhoto();
        }
    }

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Click listeners
        saveWallpaperButton.setOnClickListener(v -> {
            // Runtime Permissions
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                doFileSaving();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.CONST_WRITE_EXTERNAL_STORAGE);
            }
        });

        setWallpaperButton.setOnClickListener(v -> {
            // Runtime Permissions
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                File srcFile = FileUtils.getSavedFileFromInternalStorage(MainActivity.this);
                destFile = new File(FileUtils.getOutputMediaFileUri(MainActivity.this).getPath());
                try {
                    boolean copied = FileUtils.makeFileCopy(srcFile, destFile);
                    if (copied) {
                        // Successful copy
                        final Uri contentUri = FileProvider.getUriForFile(
                                getApplicationContext(),
                                getApplicationContext().getPackageName() + ".provider",
                                destFile
                        );
                        Intent i = WallpaperManager.getInstance(MainActivity.this)
                                .getCropAndSetWallpaperIntent(contentUri);
                        MainActivity.this.startActivityForResult(i, 123);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error while copying file: ", e);
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.CONST_WRITE_EXTERNAL_STORAGE);
            }
        });

        creditsView.setOnClickListener(v -> {
            presenter.showWallpaperCredits();
        });
    }

    private void doFileSaving() {
        File srcFile = FileUtils.getSavedFileFromInternalStorage(MainActivity.this);
        File destFile = new File(FileUtils.getOutputMediaFileUri(MainActivity.this).getPath());
        try {
            boolean copied = FileUtils.makeFileCopy(srcFile, destFile);
            if (copied) {
                Toast.makeText(MainActivity.this, "Wallpaper saved", Toast.LENGTH_SHORT).show();
                Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destFile));
                sendBroadcast(scanFileIntent);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while copying file: ", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.CONST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Copy from internal storage to the SD card
                    Toast.makeText(MainActivity.this, "Permission granted! Try doing what you were trying to do again?", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Alright! We won't save the file.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private View.OnTouchListener imageScrollListener = new View.OnTouchListener() {
        float downX;
        int totalX;
        int scrollByX;

        public boolean onTouch(View view, MotionEvent event) {
            // set maximum scroll amount (based on center of image)
            int maxX = ((wallpaper.getWidth() / 2))
                    - (Utils.getScreenWidth(MainActivity.this) / 2);

            // set scroll limits
            final int maxLeft = (maxX * -1);
            final int maxRight = maxX;

            float currentX;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    break;

                case MotionEvent.ACTION_MOVE:
                    currentX = event.getX();
                    scrollByX = (int) (downX - currentX);

                    // scrolling to left side of image (pic moving to the right)
                    if (currentX > downX) {
                        if (totalX == maxLeft) {
                            scrollByX = 0;
                        }
                        if (totalX > maxLeft) {
                            totalX = totalX + scrollByX;
                        }
                        if (totalX < maxLeft) {
                            scrollByX = maxLeft - (totalX - scrollByX);
                            totalX = maxLeft;
                        }
                    }

                    // scrolling to right side of image (pic moving to the left)
                    if (currentX < downX) {
                        if (totalX == maxRight) {
                            scrollByX = 0;
                        }
                        if (totalX < maxRight) {
                            totalX = totalX + scrollByX;
                        }
                        if (totalX > maxRight) {
                            scrollByX = maxRight - (totalX - scrollByX);
                            totalX = maxRight;
                        }
                    }
                    wallpaperImageView.scrollBy(scrollByX, 0);
                    downX = currentX;
                    break;
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                presenter.launchSettings(SettingsActivity.class);
                return true;
            case R.id.action_share:
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    shareWallpaper();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.CONST_WRITE_EXTERNAL_STORAGE);
                }
                return true;
            case R.id.action_refresh:
                presenter.refreshPhoto();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT && shouldScroll) {
            wallpaperImageView.scrollTo(0, 0);
            wallpaperImageView.setOnTouchListener(imageScrollListener);
        } else {
            wallpaperImageView.scrollTo(0, 0);
            wallpaperImageView.setOnTouchListener(null);
        }
    }

    private void shareWallpaper() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String shareText = "Great wallpaper from " + photo.getPhotographerName()
                + " on @getuttamapp today! " + photo.getPhotoDownloadUrl();
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(shareIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // wallpaper was set
                destFile.delete();
            }
        }
    }

    @Override
    public void displayPhoto(_Photo p) {
        // save the returned photo
        this.photo = p;

        wallpaper = FileUtils.getImageBitmap(this, photo.getPhotoFSPath());
        // shouldScroll flag
        shouldScroll = wallpaper.getWidth() > Utils.getScreenWidth(this);

        // set views
        wallpaperImageView.setImageBitmap(wallpaper);
        photographerTextView.setText(photo.getPhotographerName());

        if (getResources().getConfiguration().orientation
                != Configuration.ORIENTATION_LANDSCAPE && shouldScroll) {
            wallpaperImageView.setOnTouchListener(imageScrollListener);
        }

        if (Utils.isFirstRun(this)) {
            // Set it as the wallpaper
            try {
                WallpaperManager.getInstance(this).setBitmap(wallpaper);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // cast first notification
            NotificationUtils.pushFirstNotification(this, photo);
        }

    }

    @Override
    public void showSettings(Class settingsActivity) {
        startActivity(new Intent(this, settingsActivity));
    }

    @Override
    public void refreshPhoto() {
        startService(new Intent(this, GetPhotoService.class));
        finish();
    }

    @Override
    public void showWallpaperCredits() {
        String url = "http://unsplash.com/" + photo.getPhotographerUserName();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
