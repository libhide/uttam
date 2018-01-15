package com.ratik.uttam.ui.main;

import com.ratik.uttam.model.Photo;

/**
 * Created by Ratik on 17/10/17.
 */

public interface MainContract {

    interface View {
        void showPhoto(Photo photo);

        void onGetPhotoFailed();
    }

    interface Presenter {
        void setView(MainContract.View view);

        void getPhoto();

        void putPhoto(Photo photo);
    }
}
