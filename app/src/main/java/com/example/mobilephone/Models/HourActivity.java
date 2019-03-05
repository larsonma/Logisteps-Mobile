package com.example.mobilephone.Models;

import com.google.gson.annotations.SerializedName;

public class HourActivity {
    @SerializedName("hour")
    private int hour;

    @SerializedName("steps")
    private int steps;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}