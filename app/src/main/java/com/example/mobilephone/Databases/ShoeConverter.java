package com.example.mobilephone.Databases;

import com.example.mobilephone.Models.Shoe;
import com.google.gson.Gson;

import androidx.room.TypeConverter;

public class ShoeConverter {
    @TypeConverter
    public static String fromHourActivityJson(Shoe shoe) {
        return new Gson().toJson(shoe);
    }

    @TypeConverter
    public static Shoe fromJsonToHourActivity(String json) {
        return new Gson().fromJson(json, Shoe.class);
    }
}
