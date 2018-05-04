package com.ratik.uttam.data;

public interface PrefStore {
    void enableWallpaperAutoSet();

    boolean isAutoSetEnabled();

    boolean isFirstRun();

    void firstRunDone();
}
