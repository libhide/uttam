package com.ratik.uttam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ratik.uttam.R;

/**
 * Created by Ratik on 08/03/16.
 */
public class PrefUtils {
    public static void setAutomaticWallpaperSet(Context context, boolean b) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.key_automatic_wallpaper_set), b);
        editor.apply();
    }

    public static boolean shouldSetWallpaperAutomatically(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.key_automatic_wallpaper_set), false);
    }

    public static void setCustomSoundsState(Context context, boolean b) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.key_custom_sounds), b);
        editor.apply();
    }

    public static boolean userWantsCustomSounds(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.key_custom_sounds), false);
    }

    public static void setLEDState(Context context, boolean b) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.key_notif_led), b);
        editor.apply();
    }

    public static boolean userWantsNotificationLED(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.key_notif_led), false);
    }

//    public static boolean userWantsToFetchOverData(Context context) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//        return sp.getBoolean(context.getString(R.string.key_fetch_over_data), false);
//    }

    static boolean shouldCompressWallpaper(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.key_compress_wallpaper), false);
    }

    public static void setCompressState(Context context, boolean b) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.key_compress_wallpaper), b);
        editor.apply();
    }

    // Refresh Interval methods
//    @SuppressLint("CommitPrefEdits")
//    public static void setRefreshInterval(Context context, String value) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = sp.edit();
//        if (value.equals("daily")) {
//            editor.putString(Constants.PREF_REFRESH_INTERVAL, "Everyday");
//        } else {
//            editor.putString(Constants.PREF_REFRESH_INTERVAL, "Weekly");
//        }
//        editor.commit();
//    }

    public static String getRefreshInterval(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return stringifyRefreshInterval(sp.getString(context.getString(R.string.key_refresh_interval), "Everyday"));
    }

    // Helper methods
    private static String stringifyRefreshInterval(String str) {
        if (str.equals("Everyday")) {
            return "daily";
        } else {
            return "weekly";
        }
    }
}
