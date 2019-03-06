package com.example.mobilephone.Models;

import com.google.gson.annotations.SerializedName;

public class Shoe {
    @SerializedName("foot")
    private String foot;

    @SerializedName("size")
    private float size;

    public Shoe(String foot, float size) {
        this.foot = foot;
        this.size = size;
    }

    public String getFoot() {
        return foot;
    }

    public void setFoot(String foot) {
        this.foot = foot;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
}
