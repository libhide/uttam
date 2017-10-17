package com.ratik.uttam.model;

import com.google.gson.annotations.SerializedName;

import io.realm.annotations.PrimaryKey;

/**
 * Created by Ratik on 17/10/17.
 */

public class Photographer {

    @SerializedName("name")
    private String name;

    @PrimaryKey
    @SerializedName("username")
    private String username;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
