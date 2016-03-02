package com.ratik.unsplashify.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ratik.unsplashify.R;
import com.ratik.unsplashify.utils.FileUtils;
import com.ratik.unsplashify.utils.PhotoUtils;
import com.ratik.unsplashify.utils.Utils;

/**
 * Created by Ratik on 29/02/16.
 */
public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_show);

        // Get photo data
        final String photoUrlFull = PhotoUtils.getFullUrl(this);
        final String photoUrlRegular = PhotoUtils.getRegularUrl(this);
        String photographer = PhotoUtils.getPhotographerName(this);

        ImageView image = (ImageView) findViewById(R.id.wallpaper);
        Bitmap wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
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
    }
}
