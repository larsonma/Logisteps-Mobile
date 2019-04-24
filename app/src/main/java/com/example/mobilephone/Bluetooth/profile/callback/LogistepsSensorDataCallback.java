
package com.example.mobilephone.Bluetooth.profile.callback;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

@SuppressWarnings("ConstantConditions")
public abstract class LogistepsSensorDataCallback implements ProfileDataCallback, LogistepsSensorCallback {
    private final int SENSOR_READINGS = 15;

    @Override
    public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        if (data.size() != SENSOR_READINGS) {
            onInvalidDataReceived(device, data);
            return;
        }

        ArrayList<Integer> sensorReadings = new ArrayList<>();

        for (int i = 0; i < SENSOR_READINGS; i++) {
            sensorReadings.add(data.getIntValue(Data.FORMAT_UINT8, i));
        }
        onSensorDataRecieved(device, sensorReadings);
    }
}
