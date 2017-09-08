package com.ratik.uttam.ui;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.iap.utils.IabHelper;
import com.ratik.uttam.iap.utils.IabResult;
import com.ratik.uttam.iap.utils.Inventory;
import com.ratik.uttam.iap.utils.Purchase;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.Utils;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ratik on 29/02/16.
 */
public class ShowActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = ShowActivity.class.getSimpleName();

    // Views
    @BindView(R.id.wallpaper)
    ImageView wallpaperImageView;

    @BindView(R.id.photographerTextView)
    TextView photographerTextView;

    @BindView(R.id.wallpaperSetButton)
    ImageButton setWallpaperButton;

    @BindView(R.id.creditsContainer)
    LinearLayout creditsView;

    @BindView(R.id.adView)
    AdView adView;

    // IAP
    private IabHelper iabHelper;
    private boolean userHasRemovedAds;

    // Misc.
    private int screenWidth;
    private Bitmap wallpaper;
    private String photographer;
    private String userProfileUrl;
    private boolean shouldScroll;
    private File destFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);

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

        screenWidth = Utils.getScreenWidth(this);

        // Get photo data
        wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        photographer = PhotoUtils.getPhotographerName(this);
        userProfileUrl = PhotoUtils.getUserProf(this);

        // Is scroll required?
        shouldScroll = wallpaper.getWidth() >= screenWidth;

        // Set photo data
        wallpaperImageView.setImageBitmap(wallpaper);
        if (getResources().getConfiguration().orientation
                != Configuration.ORIENTATION_LANDSCAPE && shouldScroll) {
            wallpaperImageView.setOnTouchListener(imageScrollListener);
        }
        photographerTextView.setText(Utils.toTitleCase(photographer));

        // Click listeners
        setWallpaperButton.setOnClickListener(v -> {
            // Permission stuff for M+
            int permissionCheck = ContextCompat.checkSelfPermission(ShowActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                File srcFile = FileUtils.getSavedFileFromInternalStorage(ShowActivity.this);
                destFile = new File(FileUtils.getOutputMediaFileUri(ShowActivity.this).getPath());
                try {
                    boolean copied = FileUtils.makeFileCopy(srcFile, destFile);
                    if (copied) {
                        // Successful copy
                        final Uri contentUri = FileProvider.getUriForFile(
                                getApplicationContext(),
                                getApplicationContext().getPackageName() + ".provider",
                                destFile
                        );
                        Intent i = WallpaperManager.getInstance(ShowActivity.this)
                                .getCropAndSetWallpaperIntent(contentUri);
                        ShowActivity.this.startActivityForResult(i, 123);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error while copying file: ", e);
                }
            } else {
                ActivityCompat.requestPermissions(ShowActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.CONST_WRITE_EXTERNAL_STORAGE);
            }
        });

        creditsView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(userProfileUrl));
            startActivity(browserIntent);
        });
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
            // Toast.makeText(ShowActivity.this, "User is " + (userHasRemovedAds ? "PREMIUM" : "NOT PREMIUM"), Toast.LENGTH_SHORT).show();

            // Update UI
            if (!userHasRemovedAds) {
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }
        }
    };

    private View.OnTouchListener imageScrollListener = new View.OnTouchListener() {
        float downX;
        int totalX;
        int scrollByX;

        public boolean onTouch(View view, MotionEvent event) {
            // set maximum scroll amount (based on center of image)
            int maxX = ((wallpaper.getWidth() / 2)) - (screenWidth / 2 - 200);

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
            if (resultCode == RESULT_OK) {
                // wallpaper was set
                destFile.delete();
                finish();
            }
        }
    }
}
