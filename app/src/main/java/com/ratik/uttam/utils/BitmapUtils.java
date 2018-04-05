package com.ratik.uttam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import io.reactivex.Single;

/**
 * Created by Ratik on 26/02/16.
 */
public class BitmapUtils {

    public static Single<Bitmap> getBitmapFromFile(String path) {
        return Single.just(BitmapFactory.decodeFile(path));
    }

    public static Bitmap getBitmapFromResources(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static Bitmap createNewBitmap(int width, int height) {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    // reference - http://bit.ly/2GtgnyW
    public static Bitmap overlayIntoCentre(Bitmap blank, Bitmap wallpaper) {
        Bitmap overlayBitmap = Bitmap.createBitmap(blank.getWidth(),
                blank.getHeight(), blank.getConfig());

        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawBitmap(blank, new Matrix(), null);

        float left = (blank.getWidth() / 2) - (wallpaper.getWidth() / 2);
        float top = (blank.getHeight() / 2) - (wallpaper.getHeight() / 2);
        canvas.drawBitmap(wallpaper, left, top, null);

        return overlayBitmap;
    }
}
