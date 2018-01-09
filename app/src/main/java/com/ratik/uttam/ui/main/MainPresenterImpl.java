package com.ratik.uttam.ui.main;

import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.model.Photo;

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
    public void loadPhoto() {
        Photo photo = dataStore.getPhoto();
        view.displayPhoto(photo);
    }

    @Override
    public void setPhoto(Photo photo) {
        dataStore.putPhoto(photo);
    }

    @Override
    public void destroy() {
        // nothing
    }

    @Override
    public void launchSettings(Class settingsActivity) {
        view.showSettings(settingsActivity);
    }
}
