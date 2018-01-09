package com.ratik.uttam.utils;

import com.ratik.uttam.model.Photo;

import java.util.Random;

/**
 * Created by Ratik on 08/09/17.
 */

public class FetchUtils {
    private static final String TAG = FetchUtils.class.getSimpleName();

    public static String getRandomCategory() {
        // 2 == Buildings, 4 == Nature
        int[] categories = {2, 4};
        Random random = new Random();
        int index = random.nextInt(categories.length);
        return "" + categories[index];
    }

    public static Photo getHeroPhoto() {
        Photo photo = new Photo();
        photo.setPhotographerName("Efe Kurnaz");
        photo.setPhotographerUserName("@efekurnaz");
        photo.setPhotoFullUrl("https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?dpr=1&auto=format&fit=crop&w=1534&h=&q=60&cs=tinysrgb&crop=");
        photo.setPhoto(null);
        photo.setPhotoHtmlUrl("https://unsplash.com/photos/RnCPiXixooY");
        photo.setPhotoDownloadUrl("https://unsplash.com/photos/RnCPiXixooY/download");
        return photo;
    }
}
