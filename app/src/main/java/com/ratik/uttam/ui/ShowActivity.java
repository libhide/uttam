package com.ratik.uttam.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ratik.uttam.R;
import com.ratik.uttam.asyncs.SetWallpaperTask;
import com.ratik.uttam.listeners.LongPressListener;
import com.ratik.uttam.utils.AnimationUtils;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.PrefUtils;
import com.ratik.uttam.utils.Utils;

/**
 * Created by Ratik on 29/02/16.
 */
public class ShowActivity extends AppCompatActivity {

    private static final String TAG = ShowActivity.class.getSimpleName();

    // Views
    private View overlayView;

    // Preference variables
    private boolean setWallpaperAutomatically;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        overlayView = findViewById(R.id.overlay);

        gestureDetector = new GestureDetector(this,
                new LongPressListener(overlayView));

        // Get photo data
        final String photoUrlFull = PhotoUtils.getFullUrl(this);
        final String photoUrlRegular = PhotoUtils.getRegularUrl(this);
        String photographer = PhotoUtils.getPhotographerName(this);

        ImageView image = (ImageView) findViewById(R.id.wallpaper);
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

        overlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // User has long pressed and knows about the tip
                Utils.setLongPressedState(ShowActivity.this, true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        AnimationUtils.fadeInView(overlayView);
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        return gestureDetector.onTouchEvent(event);
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get user prefs
        setWallpaperAutomatically = PrefUtils.shouldSetWallpaperAutomatically(this);
    }
}
