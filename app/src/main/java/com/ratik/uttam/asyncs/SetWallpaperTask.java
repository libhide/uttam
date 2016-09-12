package com.ratik.uttam.asyncs;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ratik.uttam.R;

import java.io.IOException;

/**
 * Created by Ratik on 08/03/16.
 */
public class SetWallpaperTask extends AsyncTask<Bitmap, Void, Bitmap> {

    private static final String TAG = SetWallpaperTask.class.getSimpleName();

    private Context context;
    private boolean shouldShowToast;

    @SuppressLint("ShowToast")
    public SetWallpaperTask(Context context) {
        this.context = context;
    }

    @SuppressLint("ShowToast")
    public SetWallpaperTask(Context context, boolean shouldShowToast) {
        this.context = context;
        this.shouldShowToast = shouldShowToast;
    }


    @Override
    protected Bitmap doInBackground(Bitmap... bitmaps) {
        return bitmaps[0];
    }

    @Override
    protected void onPostExecute(Bitmap wallpaper) {
        super.onPostExecute(wallpaper);
        try {
            WallpaperManager.getInstance(context).setBitmap(wallpaper);
            if (shouldShowToast) {
                Toast.makeText(context, R.string.wallpaper_set_text, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
