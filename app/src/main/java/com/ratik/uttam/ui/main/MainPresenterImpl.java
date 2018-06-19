package com.ratik.uttam.ui.main;


import android.annotation.SuppressLint;

import com.ratik.uttam.data.PhotoStore;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 17/10/17.
 */

public class MainPresenterImpl implements MainContract.Presenter {

    private MainContract.View view;
    private PhotoStore photoStore;

    public MainPresenterImpl(PhotoStore photoStore) {
        this.photoStore = photoStore;
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void getPhoto() {
        photoStore.getPhoto()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        photo -> view.showPhoto(photo),
                        throwable -> view.onGetPhotoFailed()
                );
    }
}
