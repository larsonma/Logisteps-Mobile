package com.example.mobilephone.Activities.MainActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;

import com.example.mobilephone.Bluetooth.IDeviceLogger;

import java.util.UUID;

import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import no.nordicsemi.android.ble.error.GattError;
import no.nordicsemi.android.ble.utils.ParserUtils;
import no.nordicsemi.android.log.LogContract;

import static android.service.notification.Condition.stateToString;

// This class sets up the GATT server and the necessary services
public class ShoeServerManager {
    private final String TAG = "ShoeServerManager";

    /** data characteristic UUID */
    final static UUID DATA_CHARACTERISTIC_UUID = UUID.fromString("00001111-1212-efde-1523-785fef13d123");
    /** Time characteristic UUID */
    final static UUID TIME_CHARACTERISTIC_UUID = UUID.fromString("00002222-1212-efde-1523-785fef13d123");
    /** Service UUID */
    final static UUID LOGISTEPS_SERVICE_UUID = UUID.fromString("00000000-1212-efde-1523-785fef13d123");

    private BluetoothGattServer mBluetoothGattServer;
    private ShoeServerManagerCallbacks mCallbacks;
    private IDeviceLogger mLogger;
    private Handler mHandler;
    private OnServerOpenCallback mOnServerOpenCallback;
    private boolean mServerReady;

    public interface OnServerOpenCallback {
        /**
         * Method called when the GATT server was created and all services were added successfully.
         */
        void onGattServerOpen();
        /**
         * Method called when the GATT server failed to open and initialize services.
         * -1 is returned when the server failed to start.
         */
        void onGattServerFailed(final int error);
    }

    ShoeServerManager(final ShoeServerManagerCallbacks callbacks) {
        mHandler = new Handler();
        mCallbacks = callbacks;
    }

    /**
     * Sets the logger object. Logger is used to create logs in nRF Logger application.
     *
     * @param logger the logger object
     */
    public void setLogger(final IDeviceLogger logger) {
        mLogger = logger;
    }

    /**
     * Opens GATT server and creates 2 services: Data Service and Time Service.
     * The callback is called when initialization is complete.
     *
     * @param context the context.
     * @param callback optional callback notifying when all services has been added.
     */
    public void openGattServer(final Context context, final OnServerOpenCallback callback) {
        // Is the server already open?
        if (mBluetoothGattServer != null) {
            if (callback != null) {
                callback.onGattServerOpen();
            }
            return;
        }

        mOnServerOpenCallback = callback;

        final BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothGattServer = manager.openGattServer(context, mGattServerCallbacks);
        if (mBluetoothGattServer != null) {
            // Start adding services one by one. The onServiceAdded method will be called when it completes.
            addLogistepsService();
        } else {
            if (callback != null) {
                callback.onGattServerFailed(-1);
            }
            mOnServerOpenCallback = null;
        }
    }

    /**
     * Returns true if GATT server was opened and configured correctly.
     * False if hasn't been opened, was closed, of failed to start.
     */
    public boolean isServerReady() {
        return mServerReady;
    }

    /**
     * Closes the GATT server. It will also disconnect all existing connections.
     * If the service has already been closed, or hasn't been open, this method does nothing.
     */
    public void closeGattServer() {
        if (mBluetoothGattServer != null) {
            mBluetoothGattServer.close();
            mBluetoothGattServer = null;
            mOnServerOpenCallback = null;
            mServerReady = false;
        }
    }

    /**
     * This method notifies the Android that the Proximity profile will use the server connection
     * to given device. If the server hasn't been open this method does nothing.
     * The {@link #cancelConnection(BluetoothDevice)} method should be called when the connection
     * is no longer used.
     *
     * @param device the target device.
     */
    public void openConnection(final BluetoothDevice device) {
        if (mBluetoothGattServer != null) {
            mLogger.log(device, LogContract.Log.Level.VERBOSE, "[SERVER] Creating server connection...");
            mLogger.log(device, LogContract.Log.Level.DEBUG, "server.connect(device, autoConnect = true)");
            mBluetoothGattServer.connect(device, true);
        }
    }

    /**
     * Cancels the connection to the given device. This notifies Android that this profile will
     * no longer use this connection and it can be disconnected. In practice, this method does
     * not disconnect, so if the remote device decides still to use the phone's GATT server it
     * will be able to do so.
     * <p>
     * This bug/feature can be tested using a proximity tag that does not release its connection
     * when it got disconnected:
     * <ol>
     *     <li>Connect to your Proximity Tag.</li>
     *     <li>Verify that the bidirectional connection works - test the FIND ME button in
     *     nRF Toolbox and the FIND PHONE button on the tag.</li>
     *     <li>Disconnect from the tag</li>
     *     <li>When the device disappear from the list of devices click the FIND PHONE button on
     *     the tag. Your phone should still trigger an alarm, as the connection tag-&gt;phone
     *     is still active.</li>
     * </ol>
     * In order to avoid this issue make sure that your tag disconnects gently from phone when it
     * got disconnected itself.
     *
     * @param device the device that will no longer be used.
     */
    public void cancelConnection(final BluetoothDevice device) {
        if (mBluetoothGattServer != null) {
            mLogger.log(device, LogContract.Log.Level.VERBOSE, "[Server] Cancelling server connection...");
            mLogger.log(device, LogContract.Log.Level.DEBUG, "server.cancelConnection(device)");
            mBluetoothGattServer.cancelConnection(device);
        }
    }

    private void addLogistepsService() {
        /*
         * This method must be called in UI thread. It works fine on Nexus devices but if called
         * from other thread (e.g. from onServiceAdded in gatt server callback) it hangs the app.
         */
        final BluetoothGattCharacteristic deviceData = new BluetoothGattCharacteristic(DATA_CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);

        final BluetoothGattCharacteristic timeData = new BluetoothGattCharacteristic(TIME_CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PERMISSION_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);

        final BluetoothGattService deviceDataService = new BluetoothGattService(LOGISTEPS_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        deviceDataService.addCharacteristic(deviceData);
        deviceDataService.addCharacteristic(timeData);
        mBluetoothGattServer.addService(deviceDataService);
    }

    private final BluetoothGattServerCallback mGattServerCallbacks = new BluetoothGattServerCallback() {
        @Override
        public void onServiceAdded(final int status, final BluetoothGattService service) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mServerReady = true;

                if(mOnServerOpenCallback != null) {
                    mOnServerOpenCallback.onGattServerOpen();
                }
                mOnServerOpenCallback = null;
            } else {
                Log.e(TAG, "GATT Server failed to add service, status: " + status);
                if (mOnServerOpenCallback != null) {
                    mOnServerOpenCallback.onGattServerFailed(status);
                }
                mOnServerOpenCallback = null;
            }
        }

        @Override
        public void onConnectionStateChange(final BluetoothDevice device, final int status, final int newState) {
            mLogger.log(device, LogContract.Log.Level.DEBUG,
                    "[Server callback] Connection state changed with status: " + status
                            + " and new state: " + newState + " (" + stateToString(newState) + ")");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    mLogger.log(device, LogContract.Log.Level.INFO,
                            "[Server] Device with address " + device.getAddress() + " connected");
                } else {
                    mLogger.log(device, LogContract.Log.Level.INFO, "[Server] Device disconnected");
//                    mCallbacks.onAlarmStopped(device);
                }
            } else {
                mLogger.log(device, LogContract.Log.Level.ERROR, "[Server] Error " + status +
                        " (0x" + Integer.toHexString(status) + "): " + GattError.parseConnectionError(status));
            }
        }

        @Override
        public void onCharacteristicReadRequest(final BluetoothDevice device, final int requestId,
                                                final int offset, final BluetoothGattCharacteristic characteristic) {
            mLogger.log(device, LogContract.Log.Level.DEBUG,
                    "[Server callback] Read request for characteristic " + characteristic.getUuid()
                            + " (requestId=" + requestId + ", offset=" + offset + ")");
            mLogger.log(device, LogContract.Log.Level.INFO,
                    "[Server] READ request for characteristic " + characteristic.getUuid() + " received");

            //TODO: Implement custom logic for sending time
            byte[] value = characteristic.getValue();
            if (value != null && offset > 0) {
                byte[] offsetValue = new byte[value.length - offset];
                System.arraycopy(value, offset, offsetValue, 0, offsetValue.length);
                value = offsetValue;
            }
            if (value != null) {
                mLogger.log(device, LogContract.Log.Level.DEBUG,
                        "server.sendResponse(GATT_SUCCESS, value=" + ParserUtils.parse(value) + ")");
            } else {
                mLogger.log(device, LogContract.Log.Level.DEBUG, "server.sendResponse(GATT_SUCCESS, value=null)");
            }
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            mLogger.log(device, LogContract.Log.Level.VERBOSE, "[Server] Response sent");
        }

        @Override
        public void onCharacteristicWriteRequest(final BluetoothDevice device, final int requestId,
                                                 final BluetoothGattCharacteristic characteristic, final boolean preparedWrite,
                                                 final boolean responseNeeded, final int offset, final byte[] value) {
            mLogger.log(device, LogContract.Log.Level.DEBUG, "[Server callback] Write request to characteristic " + characteristic.getUuid()
                    + " (requestId=" + requestId + ", prepareWrite=" + preparedWrite + ", responseNeeded=" + responseNeeded
                    + ", offset=" + offset + ", value=" + ParserUtils.parse(value) + ")");
            final String writeType = !responseNeeded ? "WRITE NO RESPONSE" : "WRITE COMMAND";
            mLogger.log(device, LogContract.Log.Level.INFO, "[Server] " + writeType
                    + " request for characteristic " + characteristic.getUuid() + " received, value: " + ParserUtils.parse(value));

            if (offset == 0) {
                characteristic.setValue(value);
            } else {
                final byte[] currentValue = characteristic.getValue();
                final byte[] newValue = new byte[currentValue.length + value.length];
                System.arraycopy(currentValue, 0, newValue, 0, currentValue.length);
                System.arraycopy(value, 0, newValue, offset, value.length);
                characteristic.setValue(newValue);
            }

            if (!preparedWrite && value != null && value.length == 1) { // small validation
                //TODO: Implement custom logic for receiving data
//                if (value[0] != NO_ALERT[0]) {
//                    mLogger.log(device, LogContract.Log.Level.APPLICATION,
//                            "[Server] Immediate alarm request received: " + AlertLevelParser.parse(characteristic));
//                    mCallbacks.onAlarmTriggered(device);
//                } else {
//                    mLogger.log(device, LogContract.Log.Level.APPLICATION,
//                            "[Server] Immediate alarm request received: OFF");
//                    mCallbacks.onAlarmStopped(device);
//                }
            }

            mLogger.log(device, LogContract.Log.Level.DEBUG, "server.sendResponse(GATT_SUCCESS, offset="
                    + offset + ", value=" + ParserUtils.parse(value) + ")");
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
            mLogger.log(device, LogContract.Log.Level.VERBOSE, "[Server] Response sent");
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId,
                                            int offset, BluetoothGattDescriptor descriptor) {
            mLogger.log(device, LogContract.Log.Level.DEBUG,
                    "[Server callback] Write request to descriptor " + descriptor.getUuid() + " (requestId=" + requestId + ", offset=" + offset + ")");
            mLogger.log(device, LogContract.Log.Level.INFO,
                    "[Server] READ request for descriptor " + descriptor.getUuid() + " received");
            // This method is not supported
            mLogger.log(device, LogContract.Log.Level.WARNING, "[Server] Operation not supported");
            mLogger.log(device, LogContract.Log.Level.DEBUG, "[Server] server.sendResponse(GATT_REQUEST_NOT_SUPPORTED)");
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, offset, null);
            mLogger.log(device, LogContract.Log.Level.VERBOSE, "[Server] Response sent");
        }

        @Override
        public void onDescriptorWriteRequest(final BluetoothDevice device, final int requestId,
                                             final BluetoothGattDescriptor descriptor, final boolean preparedWrite,
                                             final boolean responseNeeded, final int offset, final byte[] value) {
            mLogger.log(device, LogContract.Log.Level.DEBUG, "[Server callback] Write request to descriptor " + descriptor.getUuid()
                    + " (requestId=" + requestId + ", prepareWrite=" + preparedWrite + ", responseNeeded=" + responseNeeded
                    + ", offset=" + offset + ", value=" + ParserUtils.parse(value) + ")");
            mLogger.log(device, LogContract.Log.Level.INFO, "[Server] READ request for descriptor " + descriptor.getUuid() + " received");
            // This method is not supported
            mLogger.log(device, LogContract.Log.Level.WARNING, "[Server] Operation not supported");
            mLogger.log(device, LogContract.Log.Level.DEBUG, "[Server] server.sendResponse(GATT_REQUEST_NOT_SUPPORTED)");
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, offset, null);
            mLogger.log(device, LogContract.Log.Level.VERBOSE, "[Server] Response sent");
        }

        @Override
        public void onExecuteWrite(final BluetoothDevice device, final int requestId, final boolean execute) {
            mLogger.log(device, LogContract.Log.Level.DEBUG,
                    "[Server callback] Execute write request (requestId=" + requestId + ", execute=" + execute + ")");
            // This method is not supported
            mLogger.log(device, LogContract.Log.Level.WARNING, "[Server] Operation not supported");
            mLogger.log(device, LogContract.Log.Level.DEBUG, "[Server] server.sendResponse(GATT_REQUEST_NOT_SUPPORTED)");
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, 0, null);
            mLogger.log(device, LogContract.Log.Level.VERBOSE, "[Server] Response sent");
        }
    };

    /**
     * Converts the connection state to String value.
     *
     * @param state the connection state.
     * @return The state as String.
     */
    private String stateToString(final int state) {
        switch (state) {
            case BluetoothProfile.STATE_CONNECTED:
                return "CONNECTED";
            case BluetoothProfile.STATE_CONNECTING:
                return "CONNECTING";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "DISCONNECTING";
            default:
                return "DISCONNECTED";
        }
    }
}
