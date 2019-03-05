package com.example.mobilephone.Databases;

import com.example.mobilephone.Models.StepSummary;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {StepSummary.class}, version = 1)
@TypeConverters({DateConverter.class, HourActivityConverter.class, InactiveTimeConverter.class})
public abstract class StepSummaryDatabase extends RoomDatabase {
    private static volatile StepSummaryDatabase INSTANCE;
    public abstract StepSummaryDao stepSummaryDao();
}
