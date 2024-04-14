package com.ratik.uttam.data;

import com.ratik.uttam.domain.model.PhotoOld;

import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Created by Ratik on 17/10/17.
 */

public interface PhotoStore {
    Completable putPhoto(PhotoOld photo);

    Single<PhotoOld> getPhoto();
}
