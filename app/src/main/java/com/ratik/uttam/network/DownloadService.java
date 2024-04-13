package com.ratik.uttam.network;

import com.ratik.uttam.data.model.PhotoApiModel;
import com.ratik.uttam.domain.model.PhotoType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import io.reactivex.Single;

public class DownloadService {
    private FileProvider fileProvider;

    @Inject
    public DownloadService(FileProvider fileProvider) {
        this.fileProvider = fileProvider;
    }

    private String getWallpaperFilePath(InputStream imageStream, PhotoType photoType) throws IOException {
        File imageFile = fileProvider.createFile(photoType);
        FileOutputStream output = new FileOutputStream(imageFile);
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = imageStream.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
        return imageFile.getAbsolutePath();
    }

    private URL getDownloadUrl(PhotoApiModel photoApiModel, PhotoType type) throws MalformedURLException {
        switch (type) {
            case REGULAR:
                return new URL(photoApiModel.getUrls().getRegularUrl());
            case THUMB:
                return new URL(photoApiModel.getUrls().getThumbUrl());
            default:
                return new URL(photoApiModel.getUrls().getFullUrl());
        }
    }

    private InputStream downloadImage(URL url) throws IOException {
        HttpURLConnection connection;
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        return connection.getInputStream();
    }

    public Single<String> downloadWallpaper(PhotoApiModel photoApiModel, PhotoType type) throws IOException {
        URL wallpaperUrl = getDownloadUrl(photoApiModel, type);
        InputStream imageInputStream = downloadImage(wallpaperUrl);
        String wallpaperPath = getWallpaperFilePath(imageInputStream, type);
        return Single.just(wallpaperPath);
    }
}
