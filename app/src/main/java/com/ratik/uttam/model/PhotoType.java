package com.ratik.uttam.model;

/**
 * Created by Ratik on 02/02/18.
 */

public enum PhotoType {
    FULL(0), REGULAR(1), THUMB(2);

    private final int value;

    PhotoType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
