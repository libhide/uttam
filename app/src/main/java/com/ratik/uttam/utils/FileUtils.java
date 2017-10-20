package com.ratik.uttam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
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

    public static void saveBitmapToInternalStorage(Context context, Bitmap b, String filename) {
        FileOutputStream out;
        try {
            out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception caught: ", e);
        }
    }

    public static Bitmap getBitmapFromInternalStorage(Context context, String filename) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        } catch (IOException e) {
            Log.e(TAG, "Exception caught: ", e);
        }
        return null;
    }

    public static File getFileFromInternalStorage(Context context, String filename) {
        return new File(context.getFilesDir() + "/" + filename);
    }

    public static Uri getExternalStorageOutputUri(Context context) {
        if (isExternalStorageAvailable()) {
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

    public static boolean transferFileFromInternalStorageToExternalStorage(Context context, String filename) {
        File srcFile = FileUtils.getFileFromInternalStorage(context, filename);
        File destFile = new File(FileUtils.getExternalStorageOutputUri(context).getPath());
        try {
            return FileUtils.makeFileCopy(srcFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Uri getUriForFileInExternalStorage(Context context, String filename) {
        File file = new File(FileUtils.getExternalStorageOutputUri(context).getPath());
        boolean transferred = transferFileFromInternalStorageToExternalStorage(context, filename);
        if (transferred) {
            // Successful copy
            return FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );
        }
        return null;
    }
}
