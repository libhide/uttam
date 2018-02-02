package com.ratik.uttam.utils;

import android.content.Context;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model.PhotoResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Ratik on 08/09/17.
 */

public class FetchUtils {
    private static final String TAG = FetchUtils.class.getSimpleName();

    public static Photo getHeroPhoto() {
        return new Photo.Builder()
                .setPhotoUri(null)
                .setRegularPhotoUri(null)
                .setThumbPhotoUri(null)
                .setPhotographerName(Constants.Fetch.FIRST_WALLPAPER_PHOTOGRAPHER_NAME)
                .setPhotographerUserName(Constants.Fetch.FIRST_WALLPAPER_PHOTOGRAPHER_USERNAME)
                .setPhotoFullUrl(Constants.Fetch.FIRST_WALLPAPER_FULL_URL)
                .setPhotoHtmlUrl(Constants.Fetch.FIRST_WALLPAPER_HTML_URL)
                .setPhotoDownloadUrl(Constants.Fetch.FIRST_WALLPAPER_DOWNLOAD_URL)
                .build();
    }

    /**
     * @param context   Android Context
     * @param photoType filename type ("FULL", "REGULAR" or "THUMB")
     * @return absolute path to created file
     */
    public static File createFile(Context context, String photoType) {
        File directory;

        directory = context.getDir("Uttam", Context.MODE_PRIVATE);
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("PhotoSaver", "Error creating directory " + directory);
        }

        switch (photoType) {
            case "FULL":
                return new File(directory, Constants.General.WALLPAPER_FILE_NAME);
            case "REGULAR":
                return new File(directory, Constants.General.WALLPAPER_REGULAR_FILE_NAME);
            default:
                // THUMB case
                return new File(directory, Constants.General.WALLPAPER_THUMB_FILE_NAME);
        }
    }

    /**
     * @param context   Android Context
     * @param url       url to image that needs downloading
     * @param photoType filename type ("FULL", "REGULAR" or "THUMB")
     * @return absolute path to downloaded file
     * @throws IOException if something goes wrong
     */
    private static String downloadImage(Context context, URL url, String photoType) throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            File imageFile = FetchUtils.createFile(context, photoType);
            FileOutputStream output = new FileOutputStream(imageFile);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            throw new IOException("Error downloading wallpaper image");
        }
    }

    public static Single<String> downloadWallpaperFull(Context context, PhotoResponse photoResponse) throws IOException {
        return Single.fromCallable(() -> {
            URL url = new URL(photoResponse.getUrls().getFullUrl());
            return downloadImage(context, url, "FULL");
        });
    }

    public static Single<String> downloadWallpaperRegular(Context context, PhotoResponse photoResponse) throws IOException {
        return Single.fromCallable(() -> {
            URL url = new URL(photoResponse.getUrls().getRegularUrl());
            return downloadImage(context, url, "REGULAR");
        });
    }

    public static Single<String> downloadWallpaperThumb(Context context, PhotoResponse photoResponse) throws IOException {
        return Single.fromCallable(() -> {
            URL url = new URL(photoResponse.getUrls().getThumbUrl());
            return downloadImage(context, url, "THUMB");
        });
    }

}
