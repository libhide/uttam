package com.ratik.unsplashify.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.ratik.unsplashify.R;
import com.ratik.unsplashify.utils.FileUtils;

/**
 * Created by Ratik on 29/02/16.
 */
public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_show);

        ImageView image = (ImageView) findViewById(R.id.wallpaper);
        Bitmap wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        image.setImageBitmap(wallpaper);
    }
}
