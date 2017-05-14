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

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ratik on 29/02/16.
 */
public class ShowActivity extends AppCompatActivity {

    private static final String TAG = ShowActivity.class.getSimpleName();

    private int screenWidth;

    private Bitmap wallpaper;

    // Views
    private ImageView image;
    private TextView photographerTextView;
    private ImageButton setWallpaperButton;
    private LinearLayout creditsView;

    private String photographer;
    private String userProfileUrl;

    // Helpers
    private boolean shouldScroll;
    private File destFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        screenWidth = Utils.getScreenWidth(this);

        // Get photo data
        wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        photographer = PhotoUtils.getPhotographerName(this);
        userProfileUrl = PhotoUtils.getUserProf(this);

        // Views init
        image = (ImageView) findViewById(R.id.wallpaper);
        photographerTextView = (TextView) findViewById(R.id.photographerTextView);
        setWallpaperButton = (ImageButton) findViewById(R.id.wallpaperSetButton);
        creditsView = (LinearLayout) findViewById(R.id.creditsContainer);

        // Is scroll required?
        shouldScroll = wallpaper.getWidth() >= screenWidth;

        // Set photo data
        image.setImageBitmap(wallpaper);
        if (getResources().getConfiguration().orientation
                != Configuration.ORIENTATION_LANDSCAPE && shouldScroll) {
            image.setOnTouchListener(imageScrollListener);
        }
        photographerTextView.setText(Utils.toTitleCase(photographer));

        // Click listeners
        setWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        creditsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(userProfileUrl));
                startActivity(browserIntent);
            }
        });
    }

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
        if (orientation == Configuration.ORIENTATION_PORTRAIT && shouldScroll) {
            image.scrollTo(0, 0);
            image.setOnTouchListener(imageScrollListener);
        } else {
            image.scrollTo(0, 0);
            image.setOnTouchListener(null);
        }
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
