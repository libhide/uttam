package com.ratik.uttam.data.impl;

import android.content.SharedPreferences;

import com.ratik.uttam.Constants;
import com.ratik.uttam.data.PrefStore;

public class PrefStoreImpl implements PrefStore {

    private SharedPreferences prefs;

    public PrefStoreImpl(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public void enableWallpaperAutoSet() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.Prefs.AUTO_SET, true);
        editor.apply();
    }

    @Override
    public boolean isAutoSetEnabled() {
        return prefs.getBoolean(Constants.Prefs.AUTO_SET, true);
    }

    @Override
    public boolean isFirstRun() {
        return prefs.getBoolean(Constants.Prefs.FIRST_RUN, true);
    }

    @Override
    public void firstRunDone() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.Prefs.FIRST_RUN, false);
        editor.apply();
    }
}
