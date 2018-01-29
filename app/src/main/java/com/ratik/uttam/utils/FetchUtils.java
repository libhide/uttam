package com.ratik.uttam.utils;

import com.ratik.uttam.Constants;
import com.ratik.uttam.model.Photo;

/**
 * Created by Ratik on 08/09/17.
 */

public class FetchUtils {
    private static final String TAG = FetchUtils.class.getSimpleName();

    public static Photo getHeroPhoto() {
        return new Photo.Builder()
                .setPhoto(null)
                .setPhotographerName(Constants.Fetch.FIRST_WALLPAPER_PHOTOGRAPHER_NAME)
                .setPhotographerUserName(Constants.Fetch.FIRST_WALLPAPER_PHOTOGRAPHER_USERNAME)
                .setPhotoFullUrl(Constants.Fetch.FIRST_WALLPAPER_FULL_URL)
                .setPhotoHtmlUrl(Constants.Fetch.FIRST_WALLPAPER_HTML_URL)
                .setPhotoDownloadUrl(Constants.Fetch.FIRST_WALLPAPER_DOWNLOAD_URL)
                .build();
    }
}
