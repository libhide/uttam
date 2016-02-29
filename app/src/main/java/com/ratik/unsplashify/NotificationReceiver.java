package com.ratik.unsplashify;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.ratik.unsplashify.model.Photo;
import com.ratik.unsplashify.ui.MainActivity;
import com.ratik.unsplashify.utils.BitmapUtils;
import com.ratik.unsplashify.utils.FileUtils;

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
 * Created by Ratik on 29/02/16.
 */
public class NotificationReceiver extends BroadcastReceiver {

    public static final String TAG = NotificationReceiver.class.getSimpleName();
    private static final int SHOW_WALLPAPER = 1;

    private Context context;
    private Photo photo;
    private int screenWidth;

    int mNotificationId = 001;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        screenWidth = getScreenWidth();

        getRandomPhoto();
    }

    private int getScreenWidth() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.SCREEN_WIDTH, 1080);
    }

    private void getRandomPhoto() {
        Toast.makeText(context, "Getting random photo", Toast.LENGTH_LONG).show();

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

        p.setWidth(object.getInt(Constants.CONST_WIDTH));
        p.setHeight(object.getInt(Constants.CONST_HEIGHT));
        p.setColor(object.getString(Constants.CONST_COLOR));

        JSONObject urls = object.getJSONObject(Constants.CONST_URLS);
        p.setUrlFull(urls.getString(Constants.CONST_URLS_FULL));
        p.setUrlRegular(urls.getString(Constants.CONST_URLS_REGULAR));
        p.setUrlSmall(urls.getString(Constants.CONST_URLS_SMALL));
        p.setUrlThumb(urls.getString(Constants.CONST_URLS_THUMB));

        JSONObject user = object.getJSONObject(Constants.CONST_USER);
        p.setName(user.getString(Constants.CONST_NAME));

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
                url = new URL(photo.getUrlRegular());

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

            // Send Notification
            notifyUser(wallpaper);
        }
    }

    private void notifyUser(Bitmap wallpaper) {
        // Content Intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("from_intent", true);

        // Content PendingIntent
        PendingIntent showWallpaperIntent = PendingIntent.getActivity(context,
                SHOW_WALLPAPER, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(wallpaper)
                        .setAutoCancel(true)
                        .setContentTitle("New Wallpaper!")
                        .setContentText("Photo by " + photo.getName())
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(wallpaper)
                                .setBigContentTitle("New Wallpaper!"))
                        .setContentIntent(showWallpaperIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private boolean clearFiles() {
        File dir = context.getFilesDir();
        File file = new File(dir, "wallpaper.png");
        return file.delete();
    }
}
