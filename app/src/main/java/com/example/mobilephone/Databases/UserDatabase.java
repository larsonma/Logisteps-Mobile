package com.example.mobilephone.Databases;

import com.example.mobilephone.Models.User;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class}, version = 1)
@TypeConverters(ShoeConverter.class)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
