package com.ratik.uttam.ui;

import com.ratik.uttam.data.DatabaseRealm;
import com.ratik.uttam.data.PhotoRepository;
import com.ratik.uttam.model._Photo;

import javax.inject.Inject;

/**
 * Created by Ratik on 17/10/17.
 */

public class MainPresenterImpl implements MainContract.Presenter {

    @Inject
    DatabaseRealm realm;

    private MainContract.View view;
    private PhotoRepository repository;
    private _Photo photo;

    public MainPresenterImpl(PhotoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void loadPhoto() {
        photo = repository.getPhoto();
        view.displayPhoto(photo);
    }

    @Override
    public void setPhoto(_Photo photo) {
        repository.putPhoto(photo);
    }

    @Override
    public void destroy() {
        realm.close();
    }

    @Override
    public void refreshPhoto() {
        view.refreshPhoto();
    }

    @Override
    public void savePhotoToStorage() {

    }

    @Override
    public void setPhotoAsWallpaper() {

    }

    @Override
    public void launchSettings(Class settingsActivity) {
        view.showSettings(settingsActivity);
    }

    @Override
    public void showWallpaperCredits() {
        view.showWallpaperCredits();
    }
}
