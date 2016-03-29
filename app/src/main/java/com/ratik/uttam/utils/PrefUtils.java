package com.ratik.uttam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ratik.uttam.R;

/**
 * Created by Ratik on 08/03/16.
 */
public class PrefUtils {
    public static boolean shouldSetWallpaperAutomatically(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.key_automatic_wallpaper_set), false);
    }

    public static boolean userWantsCustomSounds(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.key_custom_sounds), false);
    }

    public static boolean userWantsNotificationLED(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.key_notif_led), false);
    }

    public static boolean userWantsToFetchOverData(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.key_fetch_over_data), false);
    }
}
