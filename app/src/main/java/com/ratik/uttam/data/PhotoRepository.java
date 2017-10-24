package com.ratik.uttam.data;

import com.ratik.uttam.model.Photo;

/**
 * Created by Ratik on 17/10/17.
 */

public interface PhotoRepository {
    void putPhoto(Photo photo);

    Photo getPhoto();

    DatabaseRealm getRealm();

    void clear();
}
