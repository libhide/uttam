package com.ratik.uttam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
}
