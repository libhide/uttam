package com.ratik.uttam.data.impl;

import com.ratik.uttam.data.DatabaseRealm;
import com.ratik.uttam.data.PhotoRepository;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model._Photo;

import javax.inject.Inject;

/**
 * Created by Ratik on 17/10/17.
 */

public class PhotoRepositoryImpl implements PhotoRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public PhotoRepositoryImpl() {
        Injector.getAppComponent().inject(this);
    }

    @Override
    public void putPhoto(_Photo photo) {
        databaseRealm.add(photo);
    }

    @Override
    public void getPhoto() {

    }
}
