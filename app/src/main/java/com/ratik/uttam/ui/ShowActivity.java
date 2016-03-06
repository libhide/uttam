package com.ratik.uttam.ui;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ratik.uttam.R;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.Utils;

import java.io.IOException;

/**
 * Created by Ratik on 29/02/16.
 */
public class ShowActivity extends AppCompatActivity {

    private static final String TAG = ShowActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_show);

        // Get photo data
        final String photoUrlFull = PhotoUtils.getFullUrl(this);
        final String photoUrlRegular = PhotoUtils.getRegularUrl(this);
        String photographer = PhotoUtils.getPhotographerName(this);

        ImageView image = (ImageView) findViewById(R.id.wallpaper);
        final Bitmap wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        image.setImageBitmap(wallpaper);

        final Intent viewInBrowserIntent = new Intent(Intent.ACTION_VIEW);
        Button viewButton = (Button) findViewById(R.id.viewButton);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoUrlFull.isEmpty() || photoUrlRegular.isEmpty()) {
                    // TODO: handle error
                } else {
                    if (Utils.getScreenWidth(ShowActivity.this) > 720) {
                        viewInBrowserIntent.setData(Uri.parse(photoUrlFull));
                    } else {
                        viewInBrowserIntent.setData(Uri.parse(photoUrlRegular));
                    }
                    startActivity(viewInBrowserIntent);
                }
            }
        });

        // Set Wallpaper Button
        Button setWallpaperButton = (Button) findViewById(R.id.wallpaperSetButton);
        setWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set wallpaper
                try {
                    WallpaperManager.getInstance(ShowActivity.this).setBitmap(wallpaper);
                } catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
                // Finish the activity
                finish();
            }
        });
    }
}
