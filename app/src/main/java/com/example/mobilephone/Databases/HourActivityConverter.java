package com.example.mobilephone.Databases;

import com.example.mobilephone.Models.HourActivity;
import com.google.gson.Gson;

import androidx.room.TypeConverter;

public class HourActivityConverter {
    @TypeConverter
    public static String fromHourActivityJson(HourActivity activity) {
        return new Gson().toJson(activity);
    }

    @TypeConverter
    public static HourActivity fromJsonToHourActivity(String json) {
        return new Gson().fromJson(json, HourActivity.class);
    }
}
