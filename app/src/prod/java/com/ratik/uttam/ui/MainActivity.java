package com.ratik.uttam.ui;

import android.Manifest;
import android.app.WallpaperManager;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import com.ratik.uttam.iap.utils.IabHelper;
import com.ratik.uttam.iap.utils.IabResult;
import com.ratik.uttam.iap.utils.Inventory;
import com.ratik.uttam.iap.utils.Purchase;
import com.ratik.uttam.utils.AlarmUtils;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.PrefUtils;
import com.ratik.uttam.utils.Utils;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ratik.uttam.R.id.creditsContainer;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int WALL_JOB_ID = 1;

    // Wallpaper data
    private Bitmap wallpaper;
    private String photographer;
    private String downloadUrl;
    private String userProfileUrl;

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

    private InterstitialAd savingAd;

    // IAP
    private IabHelper iabHelper;
    public static boolean userHasRemovedAds;

    // Helpers
    private int screenWidth;
    private int screenHeight;
    private boolean shouldScroll;

    private File destFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Setting up interstitial ads
        setupAds();

        // IAP
        String base64EncodedPublicKey = getString(R.string.playstore_public_key);
        Log.d(TAG, "Creating IAB helper.");
        iabHelper = new IabHelper(this, base64EncodedPublicKey);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        iabHelper.startSetup(result -> {
            Log.d(TAG, "Setup finished.");

            if (!result.isSuccess()) {
                // Oh noes, there was a problem.
                Log.e(TAG, "Problem setting up in-app billing: " + result);
                return;
            }

            // Have we been disposed of in the meantime? If so, quit.
            if (iabHelper == null) return;

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(TAG, "Setup successful. Querying inventory.");
            try {
                iabHelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.e(TAG, "Error querying inventory. Another async operation in progress.");
            }
        });

        // Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setIcon(R.drawable.uttam);
        }

        boolean firstRun = Utils.isFirstRun(this);

        // Save the screen width for later use
        saveScreenSize();

        if (firstRun) {
            // save hero into the internal storage
            wallpaper = BitmapFactory.decodeResource(getResources(), R.drawable.uttam_hero);
            // wallpaper = BitmapUtils.cropBitmapFromCenterAndScreenSize(this, wallpaper);
            FileUtils.saveImage(this, wallpaper, "wallpaper", "png");

            // TODO: refactor
            PhotoUtils.setFullUrl(this, "https://images.unsplash.com/photo-1473970367503-7d7f8d1bf998?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=1c38107cb3e71ad1c6cb430b0343bd5f");
            PhotoUtils.setPhotographerName(this, "Martin Sanchez");
            PhotoUtils.setDownloadUrl(this, "http://unsplash.com/photos/bk4HoBc4k60/download");
            PhotoUtils.setUserProf(this, "https://unsplash.com/@mzeketv");

            // set it as the wallpaper
            try {
                WallpaperManager.getInstance(this).setBitmap(wallpaper);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // set alarm to set job for 7am daily
            AlarmUtils.setJobSetAlarm(this);

            // setup default prefs
            setupDefaultPrefs();

            // cast first notification
            NotificationUtils.pushFirstNotification(this, wallpaper);

            // update first run state
            Utils.setFirstRun(this, false);
        } else {
            // get saved image
            wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        }
    }

    private void setupDefaultPrefs() {
        // Check if device has resolution under 720p
        if (screenWidth <= 720) {
            PrefUtils.setCompressState(this, true);
        } else {
            PrefUtils.setCompressState(this, false);
        }
        PrefUtils.setAutomaticWallpaperSet(this, true);
        PrefUtils.setCustomSoundsState(this, true);
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (iabHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.e(TAG, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(Constants.SKU_REMOVE_ADS);
            userHasRemovedAds = premiumPurchase != null;
            Log.d(TAG, "Initial inventory query finished.");

            // Toast for testing
            // Toast.makeText(MainActivity.this, "User is " + (userHasRemovedAds ? "PREMIUM" : "NOT PREMIUM"), Toast.LENGTH_SHORT).show();
        }
    };

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

        // Is scroll required?
        shouldScroll = wallpaper.getWidth() > screenWidth;

        // Set data
        wallpaperImageView.setImageBitmap(wallpaper);
        if (getResources().getConfiguration().orientation
                != Configuration.ORIENTATION_LANDSCAPE && shouldScroll) {
            wallpaperImageView.setOnTouchListener(imageScrollListener);
        }
        photographerTextView.setText(Utils.toTitleCase(photographer));

        // Click listeners
        saveWallpaperButton.setOnClickListener(v -> {
            // Permission stuff for M+
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                if (userHasRemovedAds) {
                    doFileSaving();
                } else {
                    if (savingAd.isLoaded()) {
                        savingAd.show();
                        Toast.makeText(MainActivity.this,
                                "Close the ad to continue saving...",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Uttam is working in the background. Try in a bit?",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.CONST_WRITE_EXTERNAL_STORAGE);
            }
        });

        setWallpaperButton.setOnClickListener(v -> {
            // Set wallpaper
            // Permission stuff for M+
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
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(userProfileUrl));
            startActivity(browserIntent);
        });

        // Do cool stuff for L+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            setTheme(R.style.AppTheme_Fullscreen);
        }
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

    private void setupAds() {
        savingAd = new InterstitialAd(this);
        savingAd.setAdUnitId(getString(R.string.interstitial_ad_save));
        savingAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Request new ad
                requestNewInterstitial(savingAd);
                // Save wallpaper
                doFileSaving();
            }
        });

        requestNewInterstitial(savingAd);
    }

    private void requestNewInterstitial(InterstitialAd ad) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F10B72A932B17CB36CBBE69C25167324")
                .build();

        ad.loadAd(adRequest);
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
        String shareText = "Great wallpaper from " + photographer + " on @getuttamapp today! " + downloadUrl;
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(shareIntent);
    }

    @Override
    protected void onDestroy() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // wallpaper was set
                destFile.delete();
            }
        }
    }
}
