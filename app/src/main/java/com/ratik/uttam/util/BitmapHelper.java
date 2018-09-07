package com.ratik.uttam.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by Ratik on 26/02/16.
 */
public class BitmapHelper {

    private Context context;

    @Inject
    public BitmapHelper(Context context) {
        this.context = context;
    }

    public Single<Bitmap> getBitmapFromFile(String path) {
        return Single.just(BitmapFactory.decodeFile(path));
    }

    public Bitmap getBitmapFromResources(int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }
}
