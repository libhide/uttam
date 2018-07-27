package com.ratik.uttam.ui.main;

import com.ratik.uttam.model.Photo;

/**
 * Created by Ratik on 17/10/17.
 */

public interface MainContract {

    interface View {
        void showPhoto(Photo photo);

        void onGetPhotoFailed();

        void showRefetchProgress();

        void onRefetchPhotoSuccessful();

        void onRefetchPhotoFailure(Throwable t);
    }

    interface Presenter {
        void setView(MainContract.View view);

        void detachView();

        void getPhoto();

        void refetchPhoto();

        void refetchComplete();

        void refetchFailed(Throwable t);
    }
}
