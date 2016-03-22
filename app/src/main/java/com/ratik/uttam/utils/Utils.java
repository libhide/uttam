package com.ratik.uttam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ratik.uttam.Constants;

/**
 * Created by Ratik on 29/02/16.
 */
public class Utils {

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

    public static void setRefreshInterval(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.PREF_REFRESH_INTERVAL, value);
        editor.apply();
    }

    public static String getRefreshInterval(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Constants.PREF_REFRESH_INTERVAL, "daily");
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

    public static void setScreenWidth(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.SCREEN_WIDTH, value);
        editor.apply();
    }

    public static int getScreenWidth(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.SCREEN_WIDTH, 1080);
    }
}
