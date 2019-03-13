package com.example.mobilephone.Activities.MainActivity;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// This class handles the actual raw data from a bluetooth device
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private final ShoeService.ShoeBinder mService;
    private final List<BluetoothDevice> mDevices;

    DeviceAdapter(final ShoeService.ShoeBinder binder) {
        mService = binder;
        mDevices = mService.getManagedDevices();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(final View itemView) {
            super(itemView);

        }

        private void bind(final BluetoothDevice device) {
            final boolean ready = mService.isReady(device);


        }
    }
}
