package com.example.mobilephone.Models;

import com.google.gson.annotations.SerializedName;

public class ActivityTime {
    @SerializedName("hours")
    int hours;

    @SerializedName("minutes")
    int minutes;

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
