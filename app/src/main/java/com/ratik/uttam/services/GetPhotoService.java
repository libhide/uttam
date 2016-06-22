package com.ratik.uttam.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.Keys;
import com.ratik.uttam.R;
import com.ratik.uttam.asyncs.SetWallpaperTask;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.ui.MainActivity;
import com.ratik.uttam.ui.ShowActivity;
import com.ratik.uttam.utils.AlarmHelper;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.NetworkUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.PrefUtils;
import com.ratik.uttam.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Ratik on 04/03/16.
 */
public class GetPhotoService extends Service {

    private static final String TAG = GetPhotoService.class.getSimpleName();

    private static final int SHOW_WALLPAPER = 1;
    private static int WALLPAPER_NOTIF_ID = 001;

    private Context context;
    private Photo photo;

    public GetPhotoService() {
        context = GetPhotoService.this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            // We have access to the internet
            if (NetworkUtils.phoneIsOnWiFi(this)) {
                // Phone is on WiFi
                Log.i(TAG, "WiFi");
                getRandomPhoto();
            } else if (NetworkUtils.phoneIsOn3G(this) && PrefUtils.userWantsToFetchOverData(this)) {
                // Phone is on mobile data and user does not
                // mind fetching over it
                Log.i(TAG, "3G + permission");
                getRandomPhoto();
            } else {
                // Phone is on mobile data and user minds fetching over it
                Log.i(TAG, "3G + no permission");
                postponeFetch();
            }
        } else {
            // No internet
            postponeFetch();
        }
        return START_STICKY;
    }

    private void postponeFetch() {
        Log.i(TAG, "Postponing fetch by one hour");
        Utils.setAlarmDefState(this, true);

        AlarmHelper.postponeAlarm(this);

        // Stop current instance of the service
        stopSelf();
    }

    private void getRandomPhoto() {
        if (Utils.isAlarmDeferred(this)) {
            Utils.setAlarmDefState(this, false);
        }
        Log.d(TAG, "Getting random photo...");

        // Fetch URL
        String randomUrl = Constants.BASE_URL + Keys.API_KEY + "&category=" + getRandomCategory();
        Log.d(TAG, randomUrl);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(randomUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    if (response.isSuccessful()) {
                        photo = parsePhoto(jsonData);
                        new SaveWallpaperTask().execute();
                    } else {
                        // ERROR
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                } catch (JSONException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });
    }

    private Photo parsePhoto(String jsonData) throws JSONException {
        Photo p = new Photo();
        JSONObject object = new JSONObject(jsonData);

        JSONObject urls = object.getJSONObject(Constants.CONST_URLS);
        p.setUrlFull(urls.getString(Constants.CONST_URL_FULL));

        JSONObject user = object.getJSONObject(Constants.CONST_USER);
        p.setPhotographer(user.getString(Constants.CONST_NAME));
        p.setUserProf(user.getJSONObject(Constants.CONST_LINKS).getString("html"));

        JSONObject links = object.getJSONObject(Constants.CONST_LINKS);
        p.setHtmlUrl(links.getString(Constants.CONST_HTML));
        p.setDownloadUrl(links.getString(Constants.CONST_DOWNLOAD));

        return p;
    }

    private class SaveWallpaperTask extends AsyncTask<Void, Void, Void> {
        private Bitmap wallpaper;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Wallpaper
                URL url = new URL(photo.getUrlFull());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                wallpaper = BitmapFactory.decodeStream(input);
                wallpaper = BitmapUtils.scaleBitmap(context, wallpaper);
            } catch (IOException e) {
                Log.e(TAG, "Exception caught: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (wallpaper == null) {
                Log.e(TAG, "Bitmap is null, not saving.");
            }

            clearFiles();

            // Save to internal storage
            FileUtils.saveImage(context, wallpaper, "wallpaper", "png");

            // Save photo data into SharedPrefs
            PhotoUtils.setFullUrl(context, photo.getUrlFull());
            PhotoUtils.setPhotographerName(context, photo.getPhotographer());
            PhotoUtils.setDownloadUrl(context, photo.getDownloadUrl());
            PhotoUtils.setUserProf(context, photo.getUserProf());

            // Send Notification
            notifyUser(wallpaper);

            // If user wants automagical setting
            if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
                new SetWallpaperTask(context).execute(wallpaper);
            }

            // Stop the service
            stopSelf();
        }
    }

    private void notifyUser(Bitmap wallpaper) {
        // Content Intent
        Intent intent;
        if (PrefUtils.shouldSetWallpaperAutomatically(this)) {
            intent = new Intent(context, MainActivity.class);
        } else {
            intent = new Intent(context, ShowActivity.class);
        }

        // Content PendingIntent
        PendingIntent showWallpaperIntent = PendingIntent.getActivity(context,
                SHOW_WALLPAPER, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_uttam)
                        .setLargeIcon(BitmapUtils.cropToSquare(wallpaper))
                        .setAutoCancel(true)
                        .setContentTitle("New Wallpaper!")
                        .setContentText("Photo by " + photo.getPhotographer())
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(wallpaper)
                                .setBigContentTitle("New Wallpaper!"))
                        .setContentIntent(showWallpaperIntent);

        if (PrefUtils.userWantsCustomSounds(this)) {
            builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.uttam));
        }

        if (PrefUtils.userWantsNotificationLED(this)) {
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
        }

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(WALLPAPER_NOTIF_ID, builder.build());
    }

    private boolean clearFiles() {
        File dir = context.getFilesDir();
        File file = new File(dir, "wallpaper.png");
        return file.delete();
    }

    private String getRandomCategory() {
        /*
         2 == Buildings
         4 == Nature
         */
        int[] categories = {2, 4};
        Random random = new Random();
        int index = random.nextInt(categories.length);
        return "" + categories[index];
    }
}
