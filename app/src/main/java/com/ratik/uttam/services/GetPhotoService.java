package com.ratik.uttam.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.ratik.uttam.utils.FetchUtils.parsePhoto;

/**
 * Created by Ratik on 04/03/16.
 */
public class GetPhotoService extends Service {
    private static final String TAG = GetPhotoService.class.getSimpleName();

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
        getRandomPhoto();
        return START_STICKY;
    }

    private void getRandomPhoto() {
        Log.d(TAG, "Getting random photo...");

        // Fetch URL
        // String randomUrl = Constants.BASE_URL + Keys.API_KEY + "&category=" + getRandomCategory();
        // Log.d(TAG, randomUrl);

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
        final Request request = new Request.Builder()
                .url(fetchUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Nothing here
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String jsonData = "";
                        ResponseBody resBody = response.body();
                        if (resBody != null) {
                            jsonData = resBody.string();
                        }
                        photo = parsePhoto(jsonData);
                        new SaveWallpaperTask().execute();
                    } else {
                        // ERROR
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });
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

            // Stop the service
            stopSelf();
        }
    }

    private void notifyUser(Bitmap wallpaper) {
        NotificationUtils.pushNewWallpaperNotif(context, photo, wallpaper);
    }
}
