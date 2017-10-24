package com.ratik.uttam.ui.main;

import com.ratik.uttam.data.PhotoRepository;
import com.ratik.uttam.model.Photo;

/**
 * Created by Ratik on 17/10/17.
 */

public class MainPresenterImpl implements MainContract.Presenter {

    private MainContract.View view;
    private PhotoRepository repository;

    public MainPresenterImpl(PhotoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void loadPhoto() {
        Photo photo = repository.getPhoto();
        view.displayPhoto(photo);
    }

    @Override
    public void setPhoto(Photo photo) {
        repository.putPhoto(photo);
    }

    @Override
    public void destroy() {
        repository.getRealm().close();
    }

    @Override
    public void launchSettings(Class settingsActivity) {
        view.showSettings(settingsActivity);
    }
}
