package com.ratik.uttam.ui;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Display;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.asyncs.SetWallpaperTask;
import com.ratik.uttam.iap.utils.IabHelper;
import com.ratik.uttam.iap.utils.IabResult;
import com.ratik.uttam.services.GetPhotoService;
import com.ratik.uttam.utils.AlarmHelper;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.Utils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int FIRST_RUN_NOTIFICATION = 0;
    private static final int SHOW_WALLPAPER = 1;

    // Wallpaper data
    private Bitmap wallpaper;
    private String photographer;
    private String downloadUrl;
    private String userProfileUrl;

    int screenWidth;
    int screenHeight;

    // Views
    private ImageView image;
    private TextView photographerTextView;
    private ImageButton saveWallpaperButton;
    private ImageButton setWallpaperButton;
    private LinearLayout creditsContainer;

    private InterstitialAd interstitialAd;

    // IAP
    private IabHelper iabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // For IAP config
        String base64EncodedPublicKey = getString(R.string.playstore_public_key);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setIcon(R.drawable.uttam);
        }

        boolean firstRun = Utils.isFirstRun(this);

        // Save the screen width for later use
        saveScreenSize();

        // Views init
        image = (ImageView) findViewById(R.id.wallpaper);
        photographerTextView = (TextView) findViewById(R.id.photographerTextView);
        saveWallpaperButton = (ImageButton) findViewById(R.id.wallpaperSaveButton);
        setWallpaperButton = (ImageButton) findViewById(R.id.wallpaperSetButton);
        creditsContainer = (LinearLayout) findViewById(R.id.creditsContainer);

        if (firstRun) {
            // save hero into the internal storage
            wallpaper = BitmapFactory.decodeResource(getResources(), R.drawable.uttam_hero);
            wallpaper = BitmapUtils.cropBitmapFromCenterAndScreenSize(this, wallpaper);
            FileUtils.saveImage(this, wallpaper, "wallpaper", "png");

            // TODO: refactor
            PhotoUtils.setFullUrl(this, "https://images.unsplash.com/photo-1449024540548-94f5d5a59230?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&s=dec4b59ca06926527007bd98670f2800");
            PhotoUtils.setPhotographerName(this, "Mike Wilson");
            PhotoUtils.setDownloadUrl(this, "https://unsplash.com/photos/rM7B4DheQc0/download");
            PhotoUtils.setUserProf(this, "https://unsplash.com/mkwlsn");

            // set it as the wallpaper
            try {
                WallpaperManager.getInstance(this).setBitmap(wallpaper);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // set alarm
            AlarmHelper.setAlarm(this);

            // cast first notif
            sendFirstRunNotification();

            // update first run state
            Utils.setFirstRun(this, false);
        } else {
            // get saved image
            wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        }

        // IAP
        iabHelper = new IabHelper(this, base64EncodedPublicKey);
        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
                Log.d(TAG, "Success! OMG MONAYYYYY!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get photo data
        if (wallpaper == null) {
            // get saved image
            wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        }
        photographer = PhotoUtils.getPhotographerName(this);
        downloadUrl = PhotoUtils.getDownloadUrl(this);
        userProfileUrl = PhotoUtils.getUserProf(this);

        // Set data
        image.setImageBitmap(wallpaper);
        image.setOnTouchListener(imageScrollListener);
        photographerTextView.setText(Utils.toTitleCase(photographer));

        // Click listeners
        saveWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Permission stuff for M+
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    // Copy from internal storage to the SD card
                    saveFile();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.CONST_WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        setWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set wallpaper
                new SetWallpaperTask(MainActivity.this).execute(wallpaper);
            }
        });

        creditsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(userProfileUrl));
                startActivity(browserIntent);
            }
        });

        // Do cool stuff for L+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            setTheme(R.style.AppTheme_Fullscreen);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iabHelper != null) {
            try {
                iabHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.d(TAG, "There was an error disposing the IabHelper");
            }
        }
        iabHelper = null;
    }

    private void saveScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        Utils.setScreenWidth(this, screenWidth);
        Utils.setScreenHeight(this, screenHeight);
    }

    private void sendFirstRunNotification() {
        // Content Intent
        Intent intent = new Intent(this, ShowActivity.class);

        // Content PendingIntent
        PendingIntent showWallpaperIntent = PendingIntent.getActivity(this,
                SHOW_WALLPAPER, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_uttam)
                        .setLargeIcon(BitmapUtils.cropToSquare(wallpaper))
                        .setAutoCancel(true)
                        .setContentTitle("New Wallpaper!")
                        .setContentText("Photo by " + "Mike Wilson")
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(wallpaper)
                                .setBigContentTitle("New Wallpaper!"))
                        .setContentIntent(showWallpaperIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(FIRST_RUN_NOTIFICATION, mBuilder.build());
    }

    private void saveFile() {
        if (Utils.getSaveWallpaperCount(this) > 2) {
            // Show interstitial ad
            showInterstitialAdTheSaveWallpaper();
        } else {
            // Increment counter
            Utils.setSaveWallpaperCounter(this, Utils.getSaveWallpaperCount(this) + 1);
            // Save wallpaper
            doFileSaving();
        }
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

    // Interstitial Ad Code
    private void showInterstitialAdTheSaveWallpaper() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Reset save counter to 0
                Utils.setSaveWallpaperCounter(MainActivity.this, 0);
                // Save wallpaper
                doFileSaving();
            }

            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F10B72A932B17CB36CBBE69C25167324")
                .build();

        interstitialAd.loadAd(adRequest);
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
            int maxX = ((wallpaper.getWidth() / 2)) - (screenWidth / 2);

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
                    image.scrollBy(scrollByX, 0);
                    downX = currentX;
                    break;
            }
            return true;
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            image.scrollTo(0, 0);
            image.setOnTouchListener(imageScrollListener);
        } else {
            image.scrollTo(0, 0);
            image.setOnTouchListener(null);
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
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
                startService(new Intent(getBaseContext(), GetPhotoService.class));
                finish();
                return true;
            default:
                return false;
        }
    }

    private void shareWallpaper() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String shareText = "Check out this photo by " + photographer + " I'm rocking as my wallpaper! " + downloadUrl + " #uttam";
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(shareIntent);
    }
}
