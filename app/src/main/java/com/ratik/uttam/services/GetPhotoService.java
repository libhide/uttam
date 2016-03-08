package com.ratik.uttam.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.ratik.uttam.ui.ShowActivity;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FileUtils;
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
    private int screenWidth;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        screenWidth = Utils.getScreenWidth(context);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Started the service :)");
        getRandomPhoto();
        return START_NOT_STICKY;
    }

    private void getRandomPhoto() {
        // Toast.makeText(context, "Getting random photo", Toast.LENGTH_LONG).show();

        String randomUrl = Constants.BASE_URL + Keys.API_KEY;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(randomUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

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
        p.setUrlRegular(urls.getString(Constants.CONST_URL_REGULAR));

        JSONObject user = object.getJSONObject(Constants.CONST_USER);
        p.setPhotographer(user.getString(Constants.CONST_NAME));

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
                URL url;
                if (screenWidth <= 720) {
                    url = new URL(photo.getUrlRegular());
                } else {
                    url = new URL(photo.getUrlFull());
                }

                // For Testing
                // url = new URL(photo.getUrlRegular());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                wallpaper = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Log.e(TAG, "Exception caught: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (wallpaper != null) {
                wallpaper = BitmapUtils.cropBitmapFromCenterAndScreenSize(context, wallpaper);
            } else {
                Log.e(TAG, "Bitmap is null, not saving.");
            }

            clearFiles();

            // Save to internal storage
            FileUtils.saveImage(context, wallpaper, "wallpaper", "png");

            // Save photo data into SharedPrefs
            PhotoUtils.setFullUrl(context, photo.getUrlFull());
            PhotoUtils.setRegularUrl(context, photo.getUrlRegular());
            PhotoUtils.setPhotographerName(context, photo.getPhotographer());
            PhotoUtils.setDownloadUrl(context, photo.getDownloadUrl());
            PhotoUtils.setHTMLUrl(context, photo.getHtmlUrl());

            // Send Notification
            notifyUser(wallpaper);

            // If user wants automagical setting
            if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
                new SetWallpaperTask(context).execute(wallpaper);
            }

            // Stop the service
            Log.d(TAG, "Stopping the service");
            stopSelf();
        }
    }

    private void notifyUser(Bitmap wallpaper) {
        // Content Intent
        Intent intent = new Intent(context, ShowActivity.class);

        // Content PendingIntent
        PendingIntent showWallpaperIntent = PendingIntent.getActivity(context,
                SHOW_WALLPAPER, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapUtils.cropToSquare(wallpaper))
                        .setAutoCancel(true)
                        .setContentTitle("New Wallpaper!")
                        .setContentText("Photo by " + photo.getPhotographer())
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(wallpaper)
                                .setBigContentTitle("New Wallpaper!"))
                        .setContentIntent(showWallpaperIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(WALLPAPER_NOTIF_ID, mBuilder.build());
    }

    private boolean clearFiles() {
        File dir = context.getFilesDir();
        File file = new File(dir, "wallpaper.png");
        return file.delete();
    }
}
