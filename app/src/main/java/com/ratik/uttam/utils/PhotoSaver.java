package com.ratik.uttam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ratik on 09/01/18.
 */

public class PhotoSaver {

    private String directoryName = "Uttam";
    private String fileName = "wallpaper.png";
    private Context context;
    private boolean external = false;

    private File photoFile;

    public PhotoSaver(Context context) {
        this.context = context;
    }

    public PhotoSaver setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public PhotoSaver setExternal(boolean external) {
        this.external = external;
        return this;
    }

    public PhotoSaver setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public boolean isExternal() {
        return external;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createFile() {
        File directory;
        if (external) {
            directory = getAlbumStorageDir(directoryName);
        } else {
            directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        }
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("PhotoSaver", "Error creating directory " + directory);
        }

        photoFile = new File(directory, fileName);
        return photoFile;
    }

    private File getAlbumStorageDir(String albumName) {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public Bitmap load() {
        try (FileInputStream inputStream = new FileInputStream(createFile())) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteFile() {
        return photoFile.delete();
    }
}
