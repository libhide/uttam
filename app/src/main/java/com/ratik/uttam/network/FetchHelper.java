package com.ratik.uttam.network;

import android.content.Context;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.model.PhotoResponse;
import com.ratik.uttam.model.PhotoType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

import io.reactivex.Single;

public class FetchHelper {
    private static final String TAG = FetchHelper.class.getSimpleName();

    private Context context;

    @Inject
    public FetchHelper(Context context) {
        this.context = context;
    }

    /**
     * @param photoType filename type ("FULL", "REGULAR" or "THUMB")
     * @return absolute path to created file
     */
    public File createFile(PhotoType photoType) {
        File directory;

        directory = context.getDir("Uttam", Context.MODE_PRIVATE);
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("PhotoSaver", "Error creating directory " + directory);
        }

        switch (photoType) {
            case FULL:
                return new File(directory, Constants.General.WALLPAPER_FILE_NAME);
            case REGULAR:
                return new File(directory, Constants.General.WALLPAPER_REGULAR_FILE_NAME);
            case THUMB:
                return new File(directory, Constants.General.WALLPAPER_THUMB_FILE_NAME);
            default:
                return new File(directory, Constants.General.WALLPAPER_FILE_NAME);
        }
    }

    /**
     * @param url       url to image that needs downloading
     * @param photoType filename type ("FULL", "REGULAR" or "THUMB")
     * @return absolute path to downloaded file
     * @throws IOException if something goes wrong
     */
    private String downloadImage(URL url, PhotoType photoType) throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            File imageFile = createFile(photoType);
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

    public Single<String> downloadWallpaper(PhotoResponse photoResponse, PhotoType type) {
        switch (type) {
            case FULL:
                return Single.fromCallable(() -> {
                    URL url = new URL(photoResponse.getUrls().getFullUrl());
                    return downloadImage(url, type);
                });
            case REGULAR:
                return Single.fromCallable(() -> {
                    URL url = new URL(photoResponse.getUrls().getRegularUrl());
                    return downloadImage(url, type);
                });
            case THUMB:
                return Single.fromCallable(() -> {
                    URL url = new URL(photoResponse.getUrls().getThumbUrl());
                    return downloadImage(url, type);
                });
            default:
                return Single.fromCallable(() -> {
                    URL url = new URL(photoResponse.getUrls().getFullUrl());
                    return downloadImage(url, type);
                });
        }
    }
}
