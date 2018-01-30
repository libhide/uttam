package com.ratik.uttam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ratik.uttam.model.Photo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Ratik on 26/02/16.
 */
public class BitmapUtils {

    static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }

    public static Photo scaleBitmap(Context context, Photo photo) {
        int maxWidth = Utils.getScreenWidth(context);
        int maxHeight = Utils.getScreenHeight(context);

        int width = photo.getPhoto().getWidth();
        int height = photo.getPhoto().getHeight();

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) (height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) (width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        Bitmap bitmap = Bitmap.createScaledBitmap(photo.getPhoto(), width, height, true);

        // Further compression
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (PrefUtils.shouldCompressWallpaper(context)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
        }
        Bitmap newBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        photo.setPhoto(newBitmap);
        return photo;
    }
}
