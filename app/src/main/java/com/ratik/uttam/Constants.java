package com.ratik.uttam;

/**
 * Created by Ratik on 26/02/16.
 */
public class Constants {

    public class Api {
        public static final String BASE_URL = "https://api.unsplash.com/";
        public static final String COLLECTIONS = "935527,881815,791207";
    }

    public class General {
        public static final String BASE_DOMAIN = "unsplash.com";
        public static final String WALLPAPER_FILE_NAME = "wallpaper.png";
        public static final String WALLPAPER_REGULAR_FILE_NAME = "wallpaper_reg.png";
        public static final String WALLPAPER_THUMB_FILE_NAME = "wallpaper_thumb.png";
    }

    public class Fetch {
        public static final int WALLPAPER_FETCH_JOB_ID = 1;
        public static final String FIRST_WALLPAPER_ID = "FBLZt3Hw4e8";
        public static final String FIRST_WALLPAPER_PHOTOGRAPHER_NAME = "Andrew Haimerl";
        public static final String FIRST_WALLPAPER_PHOTOGRAPHER_USERNAME = "@andrew_haimerl";
        public static final String FIRST_WALLPAPER_FULL_URL = "https://images.unsplash.com/photo-1520959070944-20827755726d?ixlib=rb-0.3.5&s=0998b311d05bb5377a4938caeef0d03a&auto=format&fit=crop&w=1500&q=80";
        public static final String FIRST_WALLPAPER_HTML_URL = "https://unsplash.com/photos/" + FIRST_WALLPAPER_ID;
        public static final String FIRST_WALLPAPER_DOWNLOAD_URL = "https://unsplash.com/photos/FBLZt3Hw4e8/download?force=true";
    }

    public class Data {
        public static final String PHOTO_ID = "photo_id";
        public static final String PHOTO_URI = "photo_uri";
        public static final String PHOTO_REGULAR_URI = "photo_regular_uri";
        public static final String PHOTO_THUMB_URI = "photo_thumb_uri";
        public static final String PHOTOGRAPHER_NAME = "photographer_name";
        public static final String PHOTOGRAPHER_USERNAME = "photographer_username";
        public static final String FULL_URL = "full_url";
        public static final String DOWNLOAD_URL = "download_url";
        public static final String HTML_URL = "html_url";
    }

    public class Billing {
        public static final String SKU_REMOVE_ADS = "remove_adverts";
    }

    public class Ads {
        public static final String INTERSTITIAL_AD = "ca-app-pub-3718717081206155/1204419282";
    }

    public class Prefs {
        public static final String FIRST_RUN = "first_run";
        public static final String AUTO_SET = "setWallpaperAuto";
        public static final String DESIRED_WIDTH = "w";
        public static final String DESIRED_HEIGHT = "h";
    }

    // Notification constants
    public static final String NOTIF_CHANNEL_ID = "uttam_channel";
    public static final String NOTIF_CHANNEL_NAME = "General";
}