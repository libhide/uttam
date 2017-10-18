package com.ratik.uttam.ui;

import com.ratik.uttam.model._Photo;

/**
 * Created by Ratik on 17/10/17.
 */

public interface MainContract {

    interface View {
        void displayPhoto(_Photo photo);
        void showSettings(Class settingsActivity);
        void refreshPhoto();
        void showWallpaperCredits();
    }

    interface Presenter {
        void setView(MainContract.View view);
        void loadPhoto();
        void setPhoto(_Photo photo);

        void refreshPhoto();
        void savePhotoToStorage();
        void setPhotoAsWallpaper();

        void launchSettings(Class settingsActivity);

        void showWallpaperCredits();
    }
}
