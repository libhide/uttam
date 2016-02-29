package com.ratik.unsplashify.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ratik.unsplashify.Constants;

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

    public static void setRefreshInterval(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.PREF_REFRESH_INTERVAL, value);
        editor.apply();
    }

    public static int getRefreshInterval(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.PREF_REFRESH_INTERVAL, 5);
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
}
