package com.ratik.uttam.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.asyncs.SetWallpaperTask;
import com.ratik.uttam.receivers.NotificationReceiver;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int WALLPAPER_NOTIF_PENDING_INTENT_ID = 1;
    public static final int WALLPAPER_DEFERRED_NOTIF_PENDING_INTENT_ID = 2;
    private static final int FIRST_RUN_NOTIFICATION = 0;
    private static final int SHOW_WALLPAPER = 1;

    private int screenWidth;

    private Bitmap wallpaper;
    private boolean firstRun;

    private String photographer;
    private String downloadUrl;

    private ImageView image;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstRun = Utils.isFirstRun(this);

        // Save the screen width for later use
        saveScreenSize();

        if (firstRun) {
            // save hero into the internal storage
            wallpaper = BitmapFactory.decodeResource(getResources(), R.drawable.uttam_hero);
            wallpaper = BitmapUtils.cropBitmapFromCenterAndScreenSize(this, wallpaper);
            FileUtils.saveImage(this, wallpaper, "wallpaper", "png");

            // TODO: refactor
            PhotoUtils.setFullUrl(this, "https://images.unsplash.com/photo-1449024540548-94f5d5a59230?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&s=dec4b59ca06926527007bd98670f2800");
            PhotoUtils.setRegularUrl(this, "https://images.unsplash.com/photo-1449024540548-94f5d5a59230?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&s=dec4b59ca06926527007bd98670f2800");
            PhotoUtils.setPhotographerName(this, "Mike Wilson");
            PhotoUtils.setHTMLUrl(this, "https://unsplash.com/photos/rM7B4DheQc0");
            PhotoUtils.setDownloadUrl(this, "https://unsplash.com/photos/rM7B4DheQc0/download");

            // set it as the wallpaper
            try {
                WallpaperManager.getInstance(this).setBitmap(wallpaper);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // set alarm
            setNotification();

            // cast first notif
            sendFirstRunNotification();

            // update first run state
            Utils.setFirstRun(this, false);
        } else {
            // get saved image
            wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        }

        // Get photo data
        photographer = PhotoUtils.getPhotographerName(this);
        downloadUrl = PhotoUtils.getDownloadUrl(this);

        // Set ImageView
        image = (ImageView) findViewById(R.id.wallpaper);
        final Bitmap wallpaper = FileUtils.getImageBitmap(this, "wallpaper", "png");
        image.setImageBitmap(wallpaper);

        // Setup Textviews
        TextView photographerTextView = (TextView) findViewById(R.id.photographerTextView);
        photographerTextView.setText(photographer);

        // Save Wallpaper Button
        ImageButton saveWallpaperButton = (ImageButton) findViewById(R.id.wallpaperSaveButton);
        saveWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Permission stuff for M+
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    // Copy from internal storage to the SD card
                    saveFile();
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.CONST_WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        // Set Wallpaper Button
        ImageButton setWallpaperButton = (ImageButton) findViewById(R.id.wallpaperSetButton);
        setWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set wallpaper
                new SetWallpaperTask(MainActivity.this).execute(wallpaper);
            }
        });

        image.setOnTouchListener(imageScrollListener);

        // Do cool transparent stuff for new devices
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            setTheme(R.style.AppTheme_Fullscreen);
        }
    }

    private void setNotification() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                WALLPAPER_NOTIF_PENDING_INTENT_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Utils.getRefreshInterval(this).equals("daily")) {
            // Setting calendar to next day, 7 AM
            if (itIsBeforeSeven(calendar)) {
               // nothing
            } else {
                int currentDay = calendar.get(Calendar.DATE);
                calendar.set(Calendar.DATE, currentDay + 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            int currentDay = calendar.get(Calendar.DATE);
            calendar.set(Calendar.DATE, currentDay + 1);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }

        // Saving alarm-set state
        Utils.setAlarmState(this, true);
    }

    // TODO: refactor name
    private boolean itIsBeforeSeven(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 7) {
            return false;
        } else {
            return true;
        }
    }

    private void saveScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        Utils.setScreenWidth(this, screenWidth);
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
        File srcFile = FileUtils.getSavedFileFromInternalStorage(MainActivity.this);
        File destFile = new File(FileUtils.getOutputMediaFileUri(MainActivity.this).getPath());
        try {
            boolean copied = FileUtils.makeFileCopy(srcFile, destFile);
            if (copied) {
                Toast.makeText(MainActivity.this, "Image saved!", Toast.LENGTH_SHORT).show();
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
            int maxX = (wallpaper.getWidth() / 2) - (screenWidth / 2);

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

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            case R.id.action_share:
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    shareWallpaper();
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.CONST_WRITE_EXTERNAL_STORAGE);
                }
                return true;
            default:
                return false;
        }
    }

    private void shareWallpaper() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String shareText = "Check out this photo by " + photographer + " I'm rocking as my wallpaper! " + downloadUrl + " #uttam";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(shareIntent);
    }
}
