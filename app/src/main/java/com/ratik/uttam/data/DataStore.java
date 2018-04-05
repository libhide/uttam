package com.ratik.uttam.data;

import com.ratik.uttam.model.Photo;

import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Created by Ratik on 17/10/17.
 */

public interface DataStore {
    Completable putPhoto(Photo photo);

    Single<Photo> getPhoto();

    boolean isAutoSetEnabled();
}
