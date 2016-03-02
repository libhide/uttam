package com.ratik.unsplashify.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ratik.unsplashify.Constants;

/**
 * Created by Ratik on 02/03/16.
 */
public class PhotoUtils {

    public static final String filename = "photo";

    public static void setPhotographerName(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.CONST_NAME, name);
        editor.apply();
    }

    public static String getPhotographerName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return sp.getString(Constants.CONST_NAME, "Anon");
    }

    public static void setFullUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.CONST_URL_FULL, url);
        editor.apply();
    }

    public static String getFullUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return sp.getString(Constants.CONST_URL_FULL, "");
    }

    public static void setRegularUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.CONST_URL_REGULAR, url);
        editor.apply();
    }

    public static String getRegularUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return sp.getString(Constants.CONST_URL_REGULAR, "");
    }


}
