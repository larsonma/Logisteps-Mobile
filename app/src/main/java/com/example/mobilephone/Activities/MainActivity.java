package com.example.mobilephone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilephone.Bluetooth.BlinkyActivity;
import com.example.mobilephone.Bluetooth.adapter.DevicesAdapter;
import com.example.mobilephone.Bluetooth.adapter.DiscoveredBluetoothDevice;
import com.example.mobilephone.Bluetooth.utils.Utils;
import com.example.mobilephone.Bluetooth.viewmodels.ScannerStateLiveData;
import com.example.mobilephone.Bluetooth.viewmodels.ScannerViewModel;
import com.example.mobilephone.R;
import com.example.mobilephone.ViewModels.StepSummaryViewModel;
import com.example.mobilephone.ViewModels.UserViewModel;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements DevicesAdapter.OnItemClickListener{
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1022; // random number

    private ScannerViewModel mScannerViewModel;

    @BindView(R.id.state_scanning) View mScanningView;
    @BindView(R.id.no_devices)View mEmptyView;
    @BindView(R.id.no_location_permission) View mNoLocationPermissionView;
    @BindView(R.id.action_grant_location_permission) Button mGrantPermissionButton;
    @BindView(R.id.action_permission_settings) Button mPermissionSettingsButton;
    @BindView(R.id.no_location)	View mNoLocationView;
    @BindView(R.id.bluetooth_off) View mNoBluetoothView;

    // UI References
    private TextView mStepGoal;
    private TextView mStepsTaken;
    private TextView mStepsPerHr;
    private TextView mProjectedSteps;
    private TextView mLShoeStatus;
    private TextView mRShoeStatus;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private StepSummaryViewModel stepSummaryViewModel;
    private UserViewModel userViewModel;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //final Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(R.string.app_name);

        // Create view model containing utility methods for scanning
        mScannerViewModel = ViewModelProviders.of(this).get(ScannerViewModel.class);
        //mScannerViewModel.getScannerState().observe(this, this::startScan);

        // Configure the recycler view
        final RecyclerView recyclerView = findViewById(R.id.recycler_view_ble_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        final DevicesAdapter adapter = new DevicesAdapter(this, mScannerViewModel.getDevices());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        // Setup the UI
        mStepGoal = (TextView) findViewById(R.id.stepGoal);
        mStepsTaken = (TextView) findViewById(R.id.stepsTaken);
        mStepsPerHr = (TextView) findViewById(R.id.stepsPerHour);
        mProjectedSteps = (TextView) findViewById(R.id.projectedSteps);
        mLShoeStatus = (TextView) findViewById(R.id.leftShoeStatus);
        mRShoeStatus = (TextView) findViewById(R.id.rightShoeStatus);

        Button mAccountButton = (Button) findViewById(R.id.accountButton);
        mAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start new activity for account details
            }
        });

        Button mLShoeConnectButton = (Button) findViewById(R.id.leftShoeConnect);
        mLShoeConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start new service for left shoe bluetooth
                mScannerViewModel.getScannerState().observe(MainActivity.this, MainActivity.this::startScan);
            }
        });

        Button mRShoeConnectButton = (Button) findViewById(R.id.rightShoeConnect);
        mRShoeConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start new service for right shoe bluetooth
            }
        });

        this.configureDagger();
        this.configureViewModel();

        sharedPreferences = getSharedPreferences("userCredentials", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        userViewModel.init(username, password);
        stepSummaryViewModel.init();
        stepSummaryViewModel.getStepSummary().observe(this, stepSummary -> {
            if (stepSummary != null) {
                this.mStepGoal.setText("" + stepSummary.getGoal());
                this.mStepsTaken.setText("" + stepSummary.getSteps());
                this.mStepsPerHr.setText("" + (int)stepSummary.getStepsPerHour());
                this.mProjectedSteps.setText("" + stepSummaryViewModel.getProjectedSteps());
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScan();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        menu.findItem(R.id.filter_uuid).setChecked(mScannerViewModel.isUuidFilterEnabled());
        menu.findItem(R.id.filter_nearby).setChecked(mScannerViewModel.isNearbyFilterEnabled());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_uuid:
                item.setChecked(!item.isChecked());
                mScannerViewModel.filterByUuid(item.isChecked());
                return true;
            case R.id.filter_nearby:
                item.setChecked(!item.isChecked());
                mScannerViewModel.filterByDistance(item.isChecked());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(@NonNull final DiscoveredBluetoothDevice device) {
        final Intent controlBlinkIntent = new Intent(this, BlinkyActivity.class);
        controlBlinkIntent.putExtra(BlinkyActivity.EXTRA_DEVICE, device);
        startActivity(controlBlinkIntent);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION:
                mScannerViewModel.refresh();
                break;
        }
    }

    @OnClick(R.id.action_enable_location)
    public void onEnableLocationClicked() {
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @OnClick(R.id.action_enable_bluetooth)
    public void onEnableBluetoothClicked() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableIntent);
    }

    @OnClick(R.id.action_grant_location_permission)
    public void onGrantLocationPermissionClicked() {
        Utils.markLocationPermissionRequested(this);
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                REQUEST_ACCESS_COARSE_LOCATION);
    }

    @OnClick(R.id.action_permission_settings)
    public void onPermissionSettingsClicked() {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    /**
     * Start scanning for Bluetooth devices or displays a message based on the scanner state.
     */
    private void startScan(final ScannerStateLiveData state) {
        // First, check the Location permission. This is required on Marshmallow onwards in order
        // to scan for Bluetooth LE devices.
        if (Utils.isLocationPermissionsGranted(this)) {
            mNoLocationPermissionView.setVisibility(View.GONE);

            // Bluetooth must be enabled
            if (state.isBluetoothEnabled()) {
                mNoBluetoothView.setVisibility(View.GONE);

                // We are now OK to start scanning
                mScannerViewModel.startScan();
                mScanningView.setVisibility(View.VISIBLE);

                if (!state.hasRecords()) {
                    mEmptyView.setVisibility(View.VISIBLE);

                    if (!Utils.isLocationRequired(this) || Utils.isLocationEnabled(this)) {
                        mNoLocationView.setVisibility(View.INVISIBLE);
                    } else {
                        mNoLocationView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            } else {
                mNoBluetoothView.setVisibility(View.VISIBLE);
                mScanningView.setVisibility(View.INVISIBLE);
                mEmptyView.setVisibility(View.GONE);
                clear();
            }
        } else {
            mNoLocationPermissionView.setVisibility(View.VISIBLE);
            mNoBluetoothView.setVisibility(View.GONE);
            mScanningView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.GONE);

            final boolean deniedForever = Utils.isLocationPermissionDeniedForever(this);
            mGrantPermissionButton.setVisibility(deniedForever ? View.GONE : View.VISIBLE);
            mPermissionSettingsButton.setVisibility(deniedForever ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * stop scanning for bluetooth devices.
     */
    private void stopScan() {
        mScannerViewModel.stopScan();
    }

    /**
     * Clears the list of devices, which will notify the observer.
     */
    private void clear() {
        mScannerViewModel.getDevices().clear();
        mScannerViewModel.getScannerState().clearRecords();
    }

    private void configureDagger() {
        AndroidInjection.inject(this);
    }

    private void configureViewModel() {
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        stepSummaryViewModel = ViewModelProviders.of(this, viewModelFactory).get(StepSummaryViewModel.class);
    }
}
