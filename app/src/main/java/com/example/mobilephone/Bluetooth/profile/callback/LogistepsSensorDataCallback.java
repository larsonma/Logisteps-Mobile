
package com.example.mobilephone.Bluetooth.profile.callback;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

@SuppressWarnings("ConstantConditions")
public abstract class LogistepsSensorDataCallback implements ProfileDataCallback, LogistepsSensorCallback {

    @Override
    public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        if (data.size() != 2) {
            onInvalidDataReceived(device, data);
            return;
        }

        ArrayList<Integer> sensorReadings = new ArrayList<>();

        sensorReadings.add(data.getIntValue(Data.FORMAT_UINT16, 0));
        sensorReadings.add(data.getIntValue(Data.FORMAT_UINT16, 2));
        onSensorDataRecieved(device, sensorReadings);
    }
}
