package com.example.mobilephone.Databases;


import com.example.mobilephone.Models.BaseUser;
import com.google.gson.Gson;

import androidx.room.TypeConverter;

public class BaseUserConverter {
    @TypeConverter
    public static String fromBaseUser(BaseUser user) {
        return new Gson().toJson(user);
    }

    @TypeConverter
    public static BaseUser fromJsonToBaseUser(String json) {
        return new Gson().fromJson(json, BaseUser.class);
    }
}
