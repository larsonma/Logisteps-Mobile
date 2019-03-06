package com.example.mobilephone.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("user")
    private BaseUser baseUser;

    @SerializedName("left_shoe")
    private Shoe leftShoe;

    @SerializedName("right_shoe")
    private Shoe rightShoe;

    @SerializedName("height")
    private int height;

    @SerializedName("weight")
    private int weight;

    @SerializedName("step_goal")
    private int stepGoal;

    private Date lastRefresh;

    public User(BaseUser baseUser, Shoe leftShoe, Shoe rightShoe, int height, int weight, int stepGoal) {
        this.baseUser = baseUser;
        this.leftShoe = leftShoe;
        this.rightShoe = rightShoe;
        this.height = height;
        this.weight = weight;
        this.stepGoal = stepGoal;
    }

    public Date getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BaseUser getBaseUser() {
        return this.baseUser;
    }

    public void setBaseUser(BaseUser user) {
        this.baseUser = user;
    }

    public Shoe getLeftShoe() {
        return leftShoe;
    }

    public void setLeftShoe(Shoe leftShoe) {
        this.leftShoe = leftShoe;
    }

    public Shoe getRightShoe() {
        return rightShoe;
    }

    public void setRightShoe(Shoe rightShoe) {
        this.rightShoe = rightShoe;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }
}
