package com.example.mobilephone.Activities;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobilephone.Bluetooth.adapter.DevicesAdapter;
import com.example.mobilephone.Bluetooth.adapter.DiscoveredBluetoothDevice;
import com.example.mobilephone.Bluetooth.utils.Utils;
import com.example.mobilephone.Bluetooth.viewmodels.BlinkyViewModel;
import com.example.mobilephone.Bluetooth.viewmodels.ScannerStateLiveData;
import com.example.mobilephone.Bluetooth.viewmodels.ScannerViewModel;
import com.example.mobilephone.R;
import com.example.mobilephone.ViewModels.StepSummaryViewModel;
import com.example.mobilephone.ViewModels.UserViewModel;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements DevicesAdapter.OnItemClickListener{
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1022; // random number

    private ScannerViewModel mScannerViewModel;
    private BlinkyViewModel mBlinkyViewModel;

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
    private TextView mBluetoothData;
    private Button mLShoeConnectButton;
    private Button mRShoeConnectButton;
    private ConstraintLayout mStepsLayout;
    private LinearLayout mProgressContainer;
    private RecyclerView recyclerView;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private StepSummaryViewModel stepSummaryViewModel;
    private UserViewModel userViewModel;

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

        mBlinkyViewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);

        // Configure the recycler view
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        final DevicesAdapter adapter = new DevicesAdapter(this, mScannerViewModel.getDevices());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        // Setup the UI
        mStepGoal = findViewById(R.id.stepGoal);
        mStepsTaken = findViewById(R.id.stepsTaken);
        mStepsPerHr = findViewById(R.id.stepsPerHour);
        mProjectedSteps = findViewById(R.id.projectedSteps);
        mLShoeStatus = findViewById(R.id.leftShoeStatus);
        mRShoeStatus = findViewById(R.id.rightShoeStatus);
        mBluetoothData = findViewById(R.id.BluetoothData);
        mLShoeConnectButton = findViewById(R.id.leftShoeConnect);
        mRShoeConnectButton = findViewById(R.id.rightShoeConnect);

        mStepsLayout = (ConstraintLayout) findViewById(R.id.stepsLayout);
        mProgressContainer = (LinearLayout) findViewById(R.id.progress_container);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button mAccountButton = (Button) findViewById(R.id.accountButton);
        mAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start new activity for account details
            }
        });

        //Set observer for data
        mBlinkyViewModel.getButtonState().observe(this, data -> {
                this.mBluetoothData.setText("" + data);
        });

        mLShoeConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start new service for left shoe bluetooth
                if((mBlinkyViewModel.isConnected().getValue() == null) || (!mBlinkyViewModel.isConnected().getValue())) {
                    mStepsLayout.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    clear();
                    mScannerViewModel.getScannerState().observe(MainActivity.this, MainActivity.this::startScan);
                } else {
                    mBlinkyViewModel.disconnect();
                    mScannerViewModel.getScannerState().removeObserver(MainActivity.this::startScan);
                    mLShoeStatus.setTextColor(getResources().getColor(R.color.Red));
                    mLShoeStatus.setText(getString(R.string.disconnected));
                    mLShoeConnectButton.setText(getString(R.string.connect));
                    // Use method to remove observer in order to get this of MainActivity
                    removeMyObserver();
                }
            }
        });

        mRShoeConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start new service for right shoe bluetooth
            }
        });

        this.configureDagger();
        this.configureViewModel();

        Bundle userInto = getIntent().getExtras();
        userViewModel.init(userInto.getString("username"), userInto.getString("password"));
        stepSummaryViewModel.init(userViewModel.getUser(), 0);
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
        mProgressContainer.setVisibility(View.VISIBLE);

        mBlinkyViewModel.connect(device);

        mBlinkyViewModel.isConnected().observe(this, this::onDeviceConnectionChange);


        //final Intent controlBlinkIntent = new Intent(this, BlinkyActivity.class);
        //controlBlinkIntent.putExtra(BlinkyActivity.EXTRA_DEVICE, device);
        //startActivity(controlBlinkIntent);

        //TODO Start data services behind the scenes.
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

    private void onDeviceConnectionChange(final boolean connected) {
        if(connected) {
            // Connected to device user selected
            // Turn progress/recycler invisible
            // Show steps
            // Change connection status
            mProgressContainer.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            mStepsLayout.setVisibility(View.VISIBLE);
            mLShoeStatus.setText(getString(R.string.connected));
            mLShoeStatus.setTextColor(ContextCompat.getColor(this,R.color.Green));
            mLShoeConnectButton.setText(getString(R.string.disconnect));
        } else {
            // Disconnected from device
            // Change connection status
            // Remove observer
            mLShoeStatus.setText(getString(R.string.disconnected));
            mLShoeStatus.setTextColor(ContextCompat.getColor(this,R.color.Red));
            mLShoeConnectButton.setText(getString(R.string.connect));
            mBlinkyViewModel.isConnected().removeObserver(this::onDeviceConnectionChange);

        }
    }

    private void removeMyObserver() {
        mBlinkyViewModel.isConnected().removeObserver(this::onDeviceConnectionChange);
    }
}
