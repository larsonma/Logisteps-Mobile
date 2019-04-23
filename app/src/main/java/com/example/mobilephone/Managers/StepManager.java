package com.example.mobilephone.Managers;

import com.example.mobilephone.Models.Location;
import com.example.mobilephone.Models.SensorReading;
import com.example.mobilephone.Models.Step;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StepManager {

    private final int STEP_EVENT_SIZE = 15;
    private final double PRESSURE_THRESHOLD = 50.0;
    private final long TIME_THRESHOLD = 500;

    ArrayList<SensorReading> topSensorReadings;
    ArrayList<SensorReading> bottomSensorReadings;

    private long lastTopSensorUpdate;
    private long lastBottomSensorUpdate;
    private StepState stepState;

    //TODO Should have some location manager

    private OnStepCreatedEventListener onStepCreatedEventListener;

    public StepManager() {
        topSensorReadings = new ArrayList<>();
        bottomSensorReadings = new ArrayList<>();

        lastTopSensorUpdate = 0;
        lastBottomSensorUpdate = 0;
        stepState = StepState.IDLE;
    }

    public void setOnStepCreatedEventListener(OnStepCreatedEventListener eventListener) {
        onStepCreatedEventListener = eventListener;
    }

    public void addTopSensorReadings(List<SensorReading> sensorReadings) {
        //TODO try to create step if possible. If possible, call the event listener
        long now = System.currentTimeMillis();

        //Check to see if the data is a new data burst, and there is already sensor data
        //If there is, then we need to reset
        if(topSensorReadings.size() > 0) {

            if (now - lastTopSensorUpdate > TIME_THRESHOLD) {
                //Need to reset the data. A potential step was not received
                topSensorReadings.clear();
            }
        }

        topSensorReadings.addAll(sensorReadings);
        lastTopSensorUpdate = now;

        onSensorDataReceived();
    }

    public void addBottomSensorReadings(List<SensorReading> sensorReadings) {
        //TODO try to create step if possible. If possible, call the event listener
        long now = System.currentTimeMillis();

        //Check to see if the data is a new data burst, and there is already sensor data
        //If there is, then we need to reset
        if(bottomSensorReadings.size() > 0) {

            if (now - lastBottomSensorUpdate > TIME_THRESHOLD) {
                //Need to reset the data. A potential error occurred
                bottomSensorReadings.clear();
            }
        }

        bottomSensorReadings.addAll(sensorReadings);
        lastBottomSensorUpdate = now;

        onSensorDataReceived();
    }

    private double getAvgPressure(ArrayList<SensorReading> sensorReadings) {
        double average = 0;
        for (int i = 0; i < sensorReadings.size(); i++) {
            average += sensorReadings.get(i).getPressure();
        }

        if (average != 0) {
            average /= sensorReadings.size();
        }

        return average;
    }

    private void onSensorDataReceived() {
        //TODO may need to add mutex protection
        if (topSensorReadings.size() >= STEP_EVENT_SIZE && bottomSensorReadings.size() >= STEP_EVENT_SIZE) {

            // If both sensors have average pressure larger than the threshold and they occurred within a
            // small enough interval of time, create a step
            if (getAvgPressure(topSensorReadings) >= PRESSURE_THRESHOLD
                    && getAvgPressure(bottomSensorReadings) >= PRESSURE_THRESHOLD
                    && Math.abs(lastBottomSensorUpdate - lastTopSensorUpdate) < TIME_THRESHOLD
            ) {
                if (stepState != StepState.STEP_DOWN) {
                    createStep();
                }
                stepState = StepState.STEP_DOWN;
            } else {
                if (stepState == StepState.STEP_DOWN) {
                    stepState = StepState.STEP_UP;
                } else if (stepState == StepState.STEP_UP) {
                    stepState = StepState.IDLE;
                }
            }

            topSensorReadings.clear();
            bottomSensorReadings.clear();
        }
    }

    private List<SensorReading> createPressureList() {
        SensorReading topReading = new SensorReading("T", getAvgPressure(topSensorReadings));
        SensorReading bottomReading = new SensorReading("B", getAvgPressure(bottomSensorReadings));

        ArrayList<SensorReading> pressureList = new ArrayList<>();
        pressureList.add(topReading);
        pressureList.add(bottomReading);

        return pressureList;
    }

    private void createStep() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        String datetime = sdf.format(new Date());

        Location location = new Location(178.92323, -9.23422);

        Step step = new Step(datetime, createPressureList(), location);

        onStepCreatedEventListener.onStepCreated(step);
    }
}
