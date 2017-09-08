package com.ratik.uttam.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.ratik.uttam.Keys;
import com.ratik.uttam.asyncs.SetWallpaperTask;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FetchUtils;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.PhotoUtils;
import com.ratik.uttam.utils.PrefUtils;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Ratik on 26/03/17.
 */

public class GetPhotoJob extends JobService {

    // Constants
    public static final String TAG = GetPhotoJob.class.getSimpleName();

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
        private Bitmap wallpaper;
        private Photo photo;

        WallpaperTask() {
            this.context = GetPhotoJob.this;
        }

        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {
//            String randomUrl = Constants.BASE_URL
//                    + Keys.API_KEY + "&category=" + getRandomCategory();
//            Log.d(TAG, randomUrl);

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("api.unsplash.com")
                    .appendPath("photos")
                    .appendPath("random")
                    .appendQueryParameter("client_id", Keys.API_KEY)
                    .appendQueryParameter("category", FetchUtils.getRandomCategory())
                    .build();
            String fetchUrl = builder.toString();
            Log.d(TAG, fetchUrl);

            // Network call
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(fetchUrl)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String jsonData = "";
                    ResponseBody resBody = response.body();
                    if (resBody != null) {
                        jsonData = resBody.string();
                    }
                    // Save photo data
                    photo = FetchUtils.parsePhoto(jsonData);

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
            // Clean up
            FileUtils.clearFile(context, "wallpaper.png");

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

            // Finish job
            for (JobParameters parameters: params) {
                jobFinished(parameters, false);
            }
        }

        private void notifyUser(Bitmap wallpaper) {
            NotificationUtils.pushNewWallpaperNotif(context, photo, wallpaper);
        }
    }
}
