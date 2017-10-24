package com.ratik.uttam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    public static Bitmap scaleBitmap(Context context, Bitmap bm) {
        int maxWidth = Utils.getScreenWidth(context);
        int maxHeight = Utils.getScreenHeight(context);

        int width = bm.getWidth();
        int height = bm.getHeight();

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        Bitmap bitmap = Bitmap.createScaledBitmap(bm, width, height, true);

        // Further compression
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (PrefUtils.shouldCompressWallpaper(context)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
    }
}
