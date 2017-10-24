package com.ratik.uttam.data.impl;

import android.content.Context;

import com.ratik.uttam.data.DatabaseRealm;
import com.ratik.uttam.data.PhotoRepository;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Ratik on 17/10/17.
 */

public class PhotoRepositoryImpl implements PhotoRepository {

    @Inject
    Context context;

    @Inject
    DatabaseRealm databaseRealm;

    public PhotoRepositoryImpl() {
        Injector.getAppComponent().inject(this);
    }

    @Override
    public void putPhoto(Photo photo) {
        databaseRealm.add(photo);
    }

    @Override
    public Photo getPhoto() {
        List<Photo> photos = databaseRealm.findAll(Photo.class);
        return photos.get(0); // there will be only one at all times
    }

    @Override
    public DatabaseRealm getRealm() {
        return databaseRealm;
    }

    @Override
    public void clear() {
        databaseRealm.clear();
        // FileUtils.clearFile(context, "wallpaper.png");
    }
}
