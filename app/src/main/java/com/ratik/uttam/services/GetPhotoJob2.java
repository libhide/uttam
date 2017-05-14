package com.ratik.uttam.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.Keys;
import com.ratik.uttam.R;
import com.ratik.uttam.asyncs.SetWallpaperTask;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.ui.MainActivity;
import com.ratik.uttam.ui.ShowActivity;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Ratik on 26/03/17.
 */

public class GetPhotoJob2 extends JobService {
    // Constants
    public static final String TAG = GetPhotoJob2.class.getSimpleName();
    private static final int SHOW_WALLPAPER = 1;
    private static int WALLPAPER_NOTIF_ID = 001;

    private WallpaperTask wallpaperTask = null;

    @Override
    public boolean onStartJob(final JobParameters params) {
        wallpaperTask = new WallpaperTask();
        wallpaperTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (wallpaperTask != null) {
            wallpaperTask.cancel(true);
        }
        return false;
    }

    private class WallpaperTask extends AsyncTask<JobParameters, Void, JobParameters[]> {
        private Context context;
        Bitmap wallpaper;
        private Photo photo;

        WallpaperTask() {
            this.context = GetPhotoJob2.this;
        }

        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {
            String randomUrl = Constants.BASE_URL
                    + Keys.API_KEY + "&category=" + getRandomCategory();
            Log.d(TAG, randomUrl);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(randomUrl)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    photo = parsePhoto(jsonData);
                    // Save photo

                    // Get photo
                    URL url = new URL(photo.getUrlFull());

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    wallpaper = BitmapFactory.decodeStream(input);
                    wallpaper = BitmapUtils.scaleBitmap(context, wallpaper);
                } else {
                    // TODO: handle error
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Exception caught: ", e);
            }
            return params;
        }

        @Override
        protected void onPostExecute(JobParameters[] params) {
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

            // If user wants auto-magical setting
            if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
                new SetWallpaperTask(context, false).execute(wallpaper);
            }

            // finish job
            for (JobParameters parameters: params) {
                jobFinished(parameters, false);
            }
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

        private void notifyUser(Bitmap wallpaper) {
            // Content Intent
            Intent intent;
            if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
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
                            .setContentTitle(context.getString(R.string.wallpaper_notif_title))
                            .setContentText(context.getString(R.string.wallpaper_notif_photo_by) + photo.getPhotographer())
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(wallpaper)
                                    .setBigContentTitle(context.getString(R.string.wallpaper_notif_title)))
                            .setContentIntent(showWallpaperIntent);

            if (PrefUtils.userWantsCustomSounds(context)) {
                builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.uttam));
            }

            if (PrefUtils.userWantsNotificationLED(context)) {
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
}
