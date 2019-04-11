package com.example.mobilephone.Managers;

import com.example.mobilephone.Models.SensorReading;
import com.example.mobilephone.Models.Step;

import java.util.ArrayList;
import java.util.List;

public class StepManager {

    ArrayList<SensorReading> topSensorReadings;
    ArrayList<SensorReading> bottomSensorReadings;

    //TODO Should have some location manager

    private OnStepCreatedEventListener onStepCreatedEventListener;

    public StepManager() {
        topSensorReadings = new ArrayList<>();
        bottomSensorReadings = new ArrayList<>();
    }

    public void setOnStepCreatedEventListener(OnStepCreatedEventListener eventListener) {
        onStepCreatedEventListener = eventListener;
    }

    public void addTopSensorReadings(List<SensorReading> sensorReadings) {
        //TODO try to create step if possible. If possible, call the event listener
    }

    public void addBottomSensorReadings(List<SensorReading> sensorReadings) {
        //TODO try to create step if possible. If possible, call the event listener
    }

    private Step createStep() {
        return null;
    }
}
