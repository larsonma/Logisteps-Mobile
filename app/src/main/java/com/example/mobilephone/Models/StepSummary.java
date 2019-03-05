package com.example.mobilephone.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StepSummary {
    @PrimaryKey
    @NonNull
    @Expose
    @SerializedName("id")
    private int id;

    @SerializedName("steps")
    @Expose
    private int steps;

    @SerializedName("goal")
    @Expose
    private int goal;

    @SerializedName("percent")
    @Expose
    private float percent;

    @SerializedName("least_active")
    @Expose
    private HourActivity leastActiveHour;

    @SerializedName("most_active")
    @Expose
    private HourActivity mostActiveHour;

    @SerializedName("inactive_time")
    @Expose
    private ActivityTime inactiveTime;

    @SerializedName("steps_per_hour")
    @Expose
    private double stepsPerHour;

    private Date lastRefresh;

    public StepSummary(int id, int steps, int goal, float percent, HourActivity leastActiveHour,
                       HourActivity mostActiveHour, ActivityTime inactiveTime, double stepsPerHour,
                       Date lastRefresh) {
        this.id = id;
        this.steps = steps;
        this.goal = goal;
        this.percent = percent;
        this.leastActiveHour = leastActiveHour;
        this.mostActiveHour = mostActiveHour;
        this.inactiveTime = inactiveTime;
        this.stepsPerHour = stepsPerHour;
        this.lastRefresh = lastRefresh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public HourActivity getLeastActiveHour() {
        return leastActiveHour;
    }

    public void setLeastActiveHour(HourActivity leastActiveHour) {
        this.leastActiveHour = leastActiveHour;
    }

    public HourActivity getMostActiveHour() {
        return mostActiveHour;
    }

    public void setMostActiveHour(HourActivity mostActiveHour) {
        this.mostActiveHour = mostActiveHour;
    }

    public ActivityTime getInactiveTime() {
        return inactiveTime;
    }

    public void setInactiveTime(ActivityTime inactiveTime) {
        this.inactiveTime = inactiveTime;
    }

    public double getStepsPerHour() {
        return stepsPerHour;
    }

    public void setStepsPerHour(double stepsPerHour) {
        this.stepsPerHour = stepsPerHour;
    }

    public Date getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = lastRefresh;
    }
}
