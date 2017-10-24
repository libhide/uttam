package com.ratik.uttam.ui.main;

import com.ratik.uttam.model.Photo;

/**
 * Created by Ratik on 17/10/17.
 */

public interface MainContract {

    interface View {
        void displayPhoto(Photo photo);

        void showSettings(Class settingsActivity);
    }

    interface Presenter {
        void setView(MainContract.View view);

        void loadPhoto();

        void setPhoto(Photo photo);

        void destroy();

        void launchSettings(Class settingsActivity);
    }
}
