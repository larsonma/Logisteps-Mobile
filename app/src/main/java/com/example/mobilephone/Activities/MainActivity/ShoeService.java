package com.example.mobilephone.Activities.MainActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.example.mobilephone.Bluetooth.BleMulticonnectProfileService;
import com.example.mobilephone.Bluetooth.LoggableBleManager;

import java.util.List;

import androidx.annotation.NonNull;
import no.nordicsemi.android.log.LogContract;

// This class bridges the gap between the UI and the lower level background bluetooth services
public class ShoeService extends BleMulticonnectProfileService implements ShoeServerManagerCallbacks {
    @SuppressWarnings("unused")
    private static final String TAG = "ShoeService";

    public static final String BROADCAST_BATTERY_LEVEL = "no.nordicsemi.android.nrftoolbox.BROADCAST_BATTERY_LEVEL";
    public static final String EXTRA_BATTERY_LEVEL = "no.nordicsemi.android.nrftoolbox.EXTRA_BATTERY_LEVEL";

    public static final String BROADCAST_ALARM_SWITCHED = "no.nordicsemi.android.nrftoolbox.BROADCAST_ALARM_SWITCHED";
    public static final String EXTRA_ALARM_STATE = "no.nordicsemi.android.nrftoolbox.EXTRA_ALARM_STATE";

    private final static String ACTION_DISCONNECT = "no.nordicsemi.android.nrftoolbox.proximity.ACTION_DISCONNECT";
    private final static String ACTION_FIND = "no.nordicsemi.android.nrftoolbox.proximity.ACTION_FIND";
    private final static String ACTION_SILENT = "no.nordicsemi.android.nrftoolbox.proximity.ACTION_SILENT";

    private final ShoeBinder mBinder = new ShoeBinder();
    private ShoeServerManager mServerManager;

    private List<BluetoothDevice> mConnectedShoeDevices;

    private int mAttempt;
    private final static int MAX_ATTEMPTS = 1;


    public class ShoeBinder extends LocalBinder {
        // This allows events from the UI to take action on bluetooth devices
    }

    @Override
    protected LocalBinder getBinder() {
        return mBinder;
    }


    @Override
    protected LoggableBleManager<ShoeManagerCallbacks> initializeManager() {
        return new ShoeManager(this);
    }

    /**
     * This broadcast receiver listens for {@link #ACTION_DISCONNECT} that may be fired by pressing
     * Disconnect action button on the notification.
     */
    private final BroadcastReceiver mDisconnectActionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
            mBinder.log(device, LogContract.Log.Level.INFO, "[Notification] DISCONNECT action pressed");
            mBinder.disconnect(device);
        }
    };

    /**
     * This broadcast receiver listens for {@link #ACTION_FIND} or {@link #ACTION_SILENT} that may
     * be fired by pressing Find me action button on the notification.
     */
    private final BroadcastReceiver mToggleAlarmActionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
            switch (intent.getAction()) {
//                case ACTION_FIND:
//                    mBinder.log(device, LogContract.Log.Level.INFO, "[Notification] FIND action pressed");
//                    break;
//                case ACTION_SILENT:
//                    mBinder.log(device, LogContract.Log.Level.INFO, "[Notification] SILENT action pressed");
//                    break;
            }
//            mBinder.toggleImmediateAlert(device);
        }
    };

    @Override
    protected void onServiceCreated() {
        mServerManager = new ShoeServerManager(this);
        mServerManager.setLogger(mBinder);

//        initializeAlarm();

        registerReceiver(mDisconnectActionBroadcastReceiver, new IntentFilter(ACTION_DISCONNECT));
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FIND);
        filter.addAction(ACTION_SILENT);
        registerReceiver(mToggleAlarmActionBroadcastReceiver, filter);
    }

    @Override
    public void onServiceStopped() {
//        cancelNotifications();

        // Close the GATT server. If it hasn't been opened this method does nothing
        mServerManager.closeGattServer();

//        releaseAlarm();

        unregisterReceiver(mDisconnectActionBroadcastReceiver);
        unregisterReceiver(mToggleAlarmActionBroadcastReceiver);

        super.onServiceStopped();
    }

    @Override
    protected void onBluetoothEnabled() {
        mAttempt = 0;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                final Runnable that = this;
                // Start the GATT Server only if Bluetooth is enabled
                mServerManager.openGattServer(ShoeService.this,
                        new ShoeServerManager.OnServerOpenCallback() {
                            @Override
                            public void onGattServerOpen() {
                                // We are now ready to reconnect devices
                                ShoeService.super.onBluetoothEnabled();
                            }

                            @Override
                            public void onGattServerFailed(final int error) {
                                mServerManager.closeGattServer();

                                if (mAttempt < MAX_ATTEMPTS) {
                                    mAttempt++;
                                    getHandler().postDelayed(that, 2000);
                                } else {
                                    showToast("Shoe Server Error: " + error);
                                    // GATT server failed to start, but we may connect as a client
                                    ShoeService.super.onBluetoothEnabled();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onBluetoothDisabled() {
        super.onBluetoothDisabled();
        // Close the GATT server
        mServerManager.closeGattServer();
    }

    @Override
    protected void onRebind() {
        // When the activity rebinds to the service, remove the notification
//        cancelNotifications();

        // This method will read the Battery Level value from each connected device, if possible
        // and then try to enable battery notifications (if it has NOTIFY property).
        // If the Battery Level characteristic has only the NOTIFY property, it will only try to
        // enable notifications.
        //TODO: IS this needed?
//        for (final BluetoothDevice device : getManagedDevices()) {
//            final ShoeManager manager = (ShoeManager) getBleManager(device);
//            manager.readBatteryLevelCharacteristic();
//            manager.enableBatteryLevelCharacteristicNotifications();
//        }
    }

    @Override
    public void onUnbind() {
        // When we are connected, but the application is not open, we are not really interested
        // in battery level notifications. But we will still be receiving other values, if enabled.
        //TODO: Is this needed?
//        for (final BluetoothDevice device : getManagedDevices()) {
//            final ShoeManager manager = (ShoeManager) getBleManager(device);
//            manager.disableBatteryLevelCharacteristicNotifications();
//        }

    }

    @Override
    public void onDeviceConnected(final BluetoothDevice device) {
        super.onDeviceConnected(device);

        if (!mBound) {

        }
    }

    @Override
    public void onServicesDiscovered(final BluetoothDevice device, final boolean optionalServicesFound) {
        super.onServicesDiscovered(device, optionalServicesFound);
        mServerManager.openConnection(device);
    }

    @Override
    public void onLinkLossOccurred(final BluetoothDevice device) {
        mServerManager.cancelConnection(device);
        super.onLinkLossOccurred(device);

        if (!mBound) {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//                createLinkLossNotification(device);
            }
            else {
//                cancelNotification(device);
            }
        }
    }

    @Override
    public void onDeviceDisconnected(final BluetoothDevice device) {
        mServerManager.cancelConnection(device);
        super.onDeviceDisconnected(device);

        if (!mBound) {
//            cancelNotification(device);
//            createBackgroundNotification();
        }
    }

    private String getDeviceName(final BluetoothDevice device) {
        String name = device.getName();
        if (TextUtils.isEmpty(name))
            name = "Logisteps Shoe";
        return name;
    }
}
