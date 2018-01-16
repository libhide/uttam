package com.ratik.uttam.ui.main;

import com.ratik.uttam.data.DataStore;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 17/10/17.
 */

public class MainPresenterImpl implements MainContract.Presenter {

    private MainContract.View view;
    private DataStore dataStore;

    public MainPresenterImpl(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void getPhoto() {
        dataStore.getPhoto()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        photo -> view.showPhoto(photo),
                        throwable -> view.onGetPhotoFailed()
                );
    }
}
