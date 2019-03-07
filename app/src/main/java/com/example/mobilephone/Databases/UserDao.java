package com.example.mobilephone.Databases;

import androidx.lifecycle.LiveData;

import com.example.mobilephone.Models.User;

import java.util.Date;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void save(User user);
    @Query("SELECT * FROM user WHERE usr_username = :username")
    LiveData<User> load(String username);
    @Query("SELECT * FROM User WHERE usr_username == :username AND lastRefresh >= :timeout")
    User hasUser(String username, Date timeout);
}
