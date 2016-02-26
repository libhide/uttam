package com.ratik.unsplashify.ui;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ratik.unsplashify.Constants;
import com.ratik.unsplashify.Keys;
import com.ratik.unsplashify.R;
import com.ratik.unsplashify.model.Photo;
import com.ratik.unsplashify.utils.BitmapUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Photo photo;

    int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRandomPhoto();
            }
        });

        getScreenSize();
    }

    private void getRandomPhoto() {
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
                        new SetWallpaperTask().execute();
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

    private void getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        // Log.i(TAG, "Screen width " + screenWidth + "px");
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

    private class SetWallpaperTask extends AsyncTask<Void, Void, Void> {
        private Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Fetching wallpaper...",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url;
                if (screenWidth <= 720) {
                    url = new URL(photo.getUrlRegular());
                } else {
                    url = new URL(photo.getUrlFull());
                }
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Log.e(TAG, "Exception caught: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (bitmap != null) {
                try {
                    bitmap = BitmapUtils.cropBitmapFromCenterAndScreenSize(MainActivity.this, bitmap);
                    WallpaperManager.getInstance(MainActivity.this).setBitmap(bitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Wallpaper set!", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        }
    }


}
