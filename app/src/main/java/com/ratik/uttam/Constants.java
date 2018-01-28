package com.ratik.uttam;

/**
 * Created by Ratik on 26/02/16.
 */
public class Constants {

    public class API {
        public static final String BASE_URL = "https://api.unsplash.com/";
        public static final String COLLECTIONS = "420324,935527,881815,589374,1113375,791207";
    }

    public class General {
        public static final String BASE_DOMAIN = "unsplash.com";
        public static final String WALLPAPER_FILE_NAME = "wallpaper.png";
    }

    public class Fetch {
        public static final int WALLPAPER_FETCH_JOB_ID = 1;
    }

    public class Data {
        public static final String PHOTOGRAPHER_NAME = "photographer_name";
        public static final String PHOTOGRAPHER_USERNAME = "photographer_username";
        public static final String FULL_URL = "full_url";
        public static final String DOWNLOAD_URL = "download_url";
        public static final String HTML_URL = "html_url";
    }

    // Preference constants
    public static final String FIRST_RUN = "first_run";
    public static final String SCREEN_WIDTH = "screen_width";
    public static final String SCREEN_HEIGHT = "screen_height";

    // IAP constants
    public static final String SKU_REMOVE_ADS = "remove_adverts";
    public static final String REMOVE_ADS = "remove_ads";

    // Notification constants
    public static final String NOTIF_CHANNEL_ID = "uttam_channel";
    public static final String NOTIF_CHANNEL_NAME = "General";
}