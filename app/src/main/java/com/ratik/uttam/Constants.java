package com.ratik.uttam;

/**
 * Created by Ratik on 26/02/16.
 */
public class Constants {

    public class API {
        public static final String BASE_URL = "https://api.unsplash.com/";
    }

    public class General {
        public static final String BASE_URL = "https://unsplash.com/";
        public static final String WALLPAPER_FILE_NAME = "wallpaper.png";
    }

    public class Fetch {
        public static final int WALLPAPER_FETCH_JOB_ID = 1;
    }

    // Preference constants
    public static final String FIRST_RUN = "first_run";
    public static final String SCREEN_WIDTH = "screen_width";
    public static final String SCREEN_HEIGHT = "screen_width";

    // Permission constants
    public static final int CONST_WRITE_EXTERNAL_STORAGE_SAVING = 1;
    public static final int CONST_WRITE_EXTERNAL_STORAGE_SHARING = 2;

    // IAP constants
    public static final String SKU_REMOVE_ADS = "remove_adverts";
    public static final String REMOVE_ADS = "remove_ads";

    // Notification constants
    public static final String NOTIF_CHANNEL_ID = "uttam_channel";
    public static final String NOTIF_CHANNEL_NAME = "General";
}