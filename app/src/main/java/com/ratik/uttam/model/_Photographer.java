package com.ratik.uttam.model;

import com.google.gson.annotations.SerializedName;

import io.realm.annotations.PrimaryKey;

/**
 * Created by Ratik on 17/10/17.
 */

public class _Photographer {

    @SerializedName("name")
    private String name;

    @PrimaryKey
    @SerializedName("username")
    private String username;

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
