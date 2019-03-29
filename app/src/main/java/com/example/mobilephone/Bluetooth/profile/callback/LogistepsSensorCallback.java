package com.example.mobilephone.Bluetooth.profile.callback;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import androidx.annotation.NonNull;

public interface LogistepsSensorCallback {
    /**
     * Called when the data has been sent to the connected device.
     *
     * @param device the target device.
     * @param sensorReadings list of sensor readings transmitted from the shoe that describe a step.
     */
    void onSensorDataRecieved(@NonNull final BluetoothDevice device, final List<Integer> sensorReadings);
}
