package com.ratik.uttam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ratik.uttam.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ratik on 29/02/16.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static void saveImage(Context context, Bitmap b, String name, String extension) {
        name = name + "." + extension;
        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception caught: ", e);
        }
    }

    public static Bitmap getImageBitmap(Context context, String name, String extension) {
        name = name + "." + extension;
        try {
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        } catch (IOException e) {
            Log.e(TAG, "Exception caught: ", e);
        }
        return null;
    }

    public static File getSavedFileFromInternalStorage(Context context) {
        return new File(context.getFilesDir() + "/" + "wallpaper.png");
    }

    public static Uri getOutputMediaFileUri(Context context) {
        // To be safe, check that the SDCard is mounted or not
        if (isExternalStorageAvailable()) {
            // Get the external storage directory
            String appName = context.getString(R.string.app_name);
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    appName);

            // Create our sub-directory
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(TAG, "Failed to create directory.");
                    return null;
                }
            }

            // Create the file
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            mediaFile = new File(path + timestamp + ".jpg");

            // Return the file's URI
            return Uri.fromFile(mediaFile);
        } else {
            return null;
        }
    }

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean makeFileCopy(File src, File dst) throws IOException {
        if (src.getAbsolutePath().equals(dst.getAbsolutePath())) {
            return true;
        } else {
            InputStream is = new FileInputStream(src);
            OutputStream os = new FileOutputStream(dst);
            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            is.close();
            os.close();
        }
        return true;
    }

    public static boolean clearFile(Context context, String filename) {
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        return file.delete();
    }
}
