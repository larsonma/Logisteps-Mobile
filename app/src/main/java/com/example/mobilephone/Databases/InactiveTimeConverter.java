package com.example.mobilephone.Databases;

import com.example.mobilephone.Models.ActivityTime;
import com.google.gson.Gson;

import androidx.room.TypeConverter;

public class InactiveTimeConverter {
    @TypeConverter
    public static String fromInactiveTimeObjectToJson(ActivityTime time) {
        return new Gson().toJson(time);
    }

    @TypeConverter
    public static ActivityTime fromJsonToInactiveTime(String json) {
        return new Gson().fromJson(json, ActivityTime.class);
    }
}
