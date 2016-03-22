package com.ratik.uttam.utils;

import android.graphics.Bitmap;

/**
 * Created by Ratik on 26/02/16.
 */
public class BitmapUtils {

//    public static Bitmap cropBitmapFromCenterAndScreenSize(Context context, Bitmap bitmap) {
//        float screenWidth, screenHeight;
//        float bitmap_width = bitmap.getWidth(), bitmap_height = bitmap
//                .getHeight();
//        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
//                .getDefaultDisplay();
//        screenWidth = display.getWidth();
//        screenHeight = display.getHeight();
//
//        float bitmap_ratio = bitmap_width / bitmap_height;
//        float screen_ratio = screenWidth / screenHeight;
//        int bitmapNewWidth, bitmapNewHeight;
//
//        if (screen_ratio > bitmap_ratio) {
//            bitmapNewWidth = (int) screenWidth;
//            bitmapNewHeight = (int) (bitmapNewWidth / bitmap_ratio);
//        } else {
//            bitmapNewHeight = (int) screenHeight;
//            bitmapNewWidth = (int) (bitmapNewHeight * bitmap_ratio);
//        }
//
//        bitmap = Bitmap.createScaledBitmap(bitmap, bitmapNewWidth,
//                bitmapNewHeight, true);
//
//        int bitmapGapX, bitmapGapY;
//        bitmapGapX = (int) ((bitmapNewWidth - screenWidth) / 2.0f);
//        bitmapGapY = (int) ((bitmapNewHeight - screenHeight) / 2.0f);
//
//        bitmap = Bitmap.createBitmap(bitmap, bitmapGapX, bitmapGapY,
//                (int) screenWidth, (int) screenHeight);
//        return bitmap;
//    }

    public static Bitmap cropToSquare(Bitmap bitmap) {
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
}
