package com.ratik.uttam.data;

import com.ratik.uttam.model.Photo;

/**
 * Created by Ratik on 17/10/17.
 */

public interface DataStore {
    void putPhoto(Photo photo);

    Photo getPhoto();

    void clear();
}
