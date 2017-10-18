package com.ratik.uttam.data;

import com.ratik.uttam.model._Photo;

/**
 * Created by Ratik on 17/10/17.
 */

public interface PhotoRepository {
    void putPhoto(_Photo photo);
    _Photo getPhoto();
    void clear();
}
