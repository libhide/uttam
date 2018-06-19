package com.ratik.uttam.data;

public interface PrefStore {
    void enableWallpaperAutoSet();

    boolean isAutoSetEnabled();

    boolean isFirstRun();

    void firstRunDone();

    void setDesiredWallpaperWidth(int width);

    int getDesiredWallpaperWidth();

    void setDesiredWallpaperHeight(int height);

    int getDesiredWallpaperHeight();
}
