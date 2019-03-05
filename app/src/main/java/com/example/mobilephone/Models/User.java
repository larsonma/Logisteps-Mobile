package com.example.mobilephone.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    private int id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;

    private Shoe leftShoe;
    private Shoe rightShoe;

    private int height;
    private int weight;
    private int stepGoal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
