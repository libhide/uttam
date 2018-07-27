package com.ratik.uttam.ui.main;


import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.network.FetchService;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 17/10/17.
 */

public class MainPresenterImpl implements MainContract.Presenter {

    private MainContract.View view;
    private PhotoStore photoStore;
    private FetchService service;
    private CompositeDisposable compositeDisposable;

    @Inject
    public MainPresenterImpl(PhotoStore photoStore, FetchService service) {
        this.photoStore = photoStore;
        this.service = service;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void destroyView() {
        if (view != null) {
            view = null;
        }
        compositeDisposable.dispose();
    }

    @Override
    public void getPhoto() {
        compositeDisposable.add(
                photoStore.getPhoto()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                photo -> view.showPhoto(photo),
                                throwable -> view.onGetPhotoFailed()
                        )
        );
    }

    @Override
    public void refetchPhoto() {
        view.showRefetchProgress();
        compositeDisposable.add(
                service.getFetchPhotoCompletable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onRefetchPhotoSuccessful,
                                this::onRefetchPhotoFailure
                        )
        );
    }

    @Override
    public void onRefetchPhotoSuccessful() {
        view.onRefetchPhotoSuccessful();
    }

    @Override
    public void onRefetchPhotoFailure(Throwable t) {
        view.onRefetchPhotoFailure(t);
    }
}
