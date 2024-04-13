package com.ratik.uttam.ui.main;

import com.ratik.uttam.data.model.Photo;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Ratik on 17/10/17.
 */

public interface MainContract {

    interface View {
        void showPhoto(Photo photo);

        void onGetPhotoFailed();

        void showRefetchProgress();

        void hideRefetchProgress();

        void showRefetchError(String errorMessage);
    }

    interface Presenter {
        MainContract.View getView();

        void setView(MainContract.View view);

        void detachView();

        void getPhoto();

        void refetchPhoto();

        CompositeDisposable getCompositeDisposable();
    }
}
