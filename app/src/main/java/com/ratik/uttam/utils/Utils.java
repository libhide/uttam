package com.ratik.uttam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ratik.uttam.Constants;

/**
 * Created by Ratik on 29/02/16.
 */
public class Utils {

    public static void setSaveWallpaperCounter(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.SAVE_COUNTER, value);
        editor.apply();
    }

    public static int getSaveWallpaperCount(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.SAVE_COUNTER, 0);
    }

    public static void setFirstRun(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.FIRST_RUN, value);
        editor.apply();
    }

    public static boolean isFirstRun(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.FIRST_RUN, true);
    }

    public static void setAlarmState(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.ALARM_SET, value);
        editor.apply();
    }

    public static boolean isAlarmSet(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.ALARM_SET, false);
    }

    public static void setAlarmDefState(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.ALARM_DEFERRED, value);
        editor.apply();
    }

    public static boolean isAlarmDeferred(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.ALARM_DEFERRED, false);
    }

    public static void setScreenWidth(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.SCREEN_WIDTH, value);
        editor.apply();
    }

    public static int getScreenWidth(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.SCREEN_WIDTH, 1920);
    }

    public static void setScreenHeight(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.SCREEN_HEIGHT, value);
        editor.apply();
    }

    public static int getScreenHeight(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.SCREEN_HEIGHT, 1080);
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }
}
