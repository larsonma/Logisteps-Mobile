
package com.example.mobilephone.Models;

import com.example.mobilephone.Models.Location;
import com.example.mobilephone.Models.SensorReading;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Step {
    public Step(String time, String shoe, List<SensorReading> sensorReadings, Location location) {
        this.time = time;
        this.shoe = shoe;
        this.sensorReadings = sensorReadings;
        this.location = location;
    }

    public Step(String time, List<SensorReading> sensorReadings, Location location) {
        this.time = time;
        this.sensorReadings = sensorReadings;
        this.location = location;
    }

    @SerializedName("datetime")
    private String time;

    @SerializedName("shoe")
    private String shoe;

    @SerializedName("sensor_readings")
    private List<SensorReading> sensorReadings;

    @SerializedName("location")
    private Location location;

    public void setShoe(String shoe) {
        this.shoe = shoe;
    }
}
