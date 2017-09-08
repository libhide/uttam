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
}
