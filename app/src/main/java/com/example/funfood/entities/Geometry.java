package com.example.funfood.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Geometry implements Serializable {
    @SerializedName("location")
    private ResultLocation location;

    public ResultLocation getLocation() {
        return location;
    }

    public void setLocation(ResultLocation location) {
        this.location = location;
    }
}
