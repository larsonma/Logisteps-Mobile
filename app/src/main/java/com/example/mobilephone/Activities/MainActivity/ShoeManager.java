package com.example.mobilephone.Activities.MainActivity;

import android.content.Context;

import com.example.mobilephone.Bluetooth.LoggableBleManager;

import androidx.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class ShoeManager<T extends ShoeManagerCallbacks> extends LoggableBleManager<T> {

    ShoeManager(final Context context) {
        super(context);

    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return null;
    }
}
