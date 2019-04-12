package com.example.mobilephone.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SensorReading {
    @SerializedName("location")
    private String location;

    @SerializedName("pressure")
    private double pressure;


    public SensorReading(String location, double pressure) {
        this.location = location;
        this.pressure = pressure;
    }

    public double getPressure() {
        return pressure;
    }
}
