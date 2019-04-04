package com.example.mobilephone.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;
}
