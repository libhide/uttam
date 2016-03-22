package com.ratik.uttam.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.asyncs.SetWallpaperTask;
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

    private Bitmap wallpaper;

    // Views
    private ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        // Get photo data
        final String photoUrlFull = PhotoUtils.getFullUrl(this);
        final String photoUrlRegular = PhotoUtils.getRegularUrl(this);
        String photographer = PhotoUtils.getPhotographerName(this);

        image = (ImageView) findViewById(R.id.wallpaper);
        final Bitmap wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        image.setImageBitmap(wallpaper);

        // Setup Textviews
        TextView photographerTextView = (TextView) findViewById(R.id.photographerTextView);
        photographerTextView.setText(photographer);

        Button viewButton = (Button) findViewById(R.id.viewButton);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewInBrowserIntent = new Intent(Intent.ACTION_VIEW);
                viewInBrowserIntent.setData(Uri.parse(PhotoUtils.getHTMLUrl(ShowActivity.this)));
                startActivity(viewInBrowserIntent);
            }
        });

        // Set Wallpaper Button
        ImageButton setWallpaperButton = (ImageButton) findViewById(R.id.wallpaperSetButton);
        setWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetWallpaperTask(ShowActivity.this).execute(wallpaper);
                // Finish the activity
                finish();
            }
        });

        // Save Wallpaper Button
        ImageButton saveWallpaperButton = (ImageButton) findViewById(R.id.wallpaperSaveButton);
        saveWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Permission stuff for M+
                int permissionCheck = ContextCompat.checkSelfPermission(ShowActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    // Copy from internal storage to the SD card
                    saveFile();
                }
                else {
                    ActivityCompat.requestPermissions(ShowActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.CONST_WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        image.setOnTouchListener(imageScrollListener);
    }

    private void saveFile() {
        File srcFile = FileUtils.getSavedFileFromInternalStorage(ShowActivity.this);
        File destFile = new File(FileUtils.getOutputMediaFileUri(ShowActivity.this).getPath());
        try {
            boolean copied = FileUtils.makeFileCopy(srcFile, destFile);
            if (copied) {
                Toast.makeText(ShowActivity.this, "Image saved!", Toast.LENGTH_SHORT).show();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.parse(destFile.getAbsolutePath()));
                sendBroadcast(mediaScanIntent);
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
                    saveFile();
                } else {
                    Toast.makeText(ShowActivity.this, "Alright! We won't save the file.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private View.OnTouchListener imageScrollListener = new View.OnTouchListener() {
        float downX;
        int totalX;
        int scrollByX;

        public boolean onTouch(View view, MotionEvent event) {
            // set maximum scroll amount (based on center of image)
            int maxX = (wallpaper.getWidth() / 2) - (Utils.getScreenWidth(ShowActivity.this) / 2);

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
                    scrollByX = (int)(downX - currentX);

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
}
