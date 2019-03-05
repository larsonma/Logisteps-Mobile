package com.example.mobilephone.Databases;

import androidx.lifecycle.LiveData;

import com.example.mobilephone.Models.StepSummary;

import java.util.Date;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface StepSummaryDao {
    @Insert(onConflict = REPLACE)
    void save(StepSummary stepSummary);
    @Query("SELECT * FROM StepSummary WHERE id = :summaryId")
    LiveData<StepSummary> load(int summaryId);
    @Query("SELECT * FROM StepSummary WHERE id == :summaryId AND lastRefresh >= :timeout")
    StepSummary hasStepSummary(int summaryId, Date timeout);
}
