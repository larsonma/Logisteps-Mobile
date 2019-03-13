package com.example.mobilephone.Activities.MainActivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.AndroidInjection;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilephone.Bluetooth.BleMulticonnectProfileService;
import com.example.mobilephone.Bluetooth.BleMulticonnectProfileServiceReadyActivity;
import com.example.mobilephone.R;
import com.example.mobilephone.ViewModels.StepSummaryViewModel;
import com.example.mobilephone.ViewModels.UserViewModel;

import java.util.UUID;

import javax.inject.Inject;

public class MainActivity extends BleMulticonnectProfileServiceReadyActivity<ShoeService.ShoeBinder> {

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


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Setup the UI
//        mStepGoal = (TextView) findViewById(R.id.stepGoal);
//        mStepsTaken = (TextView) findViewById(R.id.stepsTaken);
//        mStepsPerHr = (TextView) findViewById(R.id.stepsPerHour);
//        mProjectedSteps = (TextView) findViewById(R.id.projectedSteps);
//        mLShoeStatus = (TextView) findViewById(R.id.leftShoeStatus);
//        mRShoeStatus = (TextView) findViewById(R.id.rightShoeStatus);
//
//        Button mAccountButton = (Button) findViewById(R.id.accountButton);
//        mAccountButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: start new activity for account details
//            }
//        });
//
//        Button mLShoeConnectButton = (Button) findViewById(R.id.leftShoeConnect);
//        mLShoeConnectButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: start new service for left shoe bluetooth
//            }
//        });
//
//        Button mRShoeConnectButton = (Button) findViewById(R.id.rightShoeConnect);
//        mRShoeConnectButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: start new service for right shoe bluetooth
//            }
//        });
//
//        this.configureDagger();
//        this.configureViewModel();
//
//        Bundle userInto = getIntent().getExtras();
//        userViewModel.init(userInto.getString("username"), userInto.getString("password"));
//        stepSummaryViewModel.init(userViewModel.getUser(), 0);
//        stepSummaryViewModel.getStepSummary().observe(this, stepSummary -> {
//            if (stepSummary != null) {
//                this.mStepGoal.setText("" + stepSummary.getGoal());
//                this.mStepsTaken.setText("" + stepSummary.getSteps());
//                this.mStepsPerHr.setText("" + (int)stepSummary.getStepsPerHour());
//                this.mProjectedSteps.setText("" + stepSummaryViewModel.getProjectedSteps());
//            }
//        });
//    }

    private void configureDagger() {
        AndroidInjection.inject(this);
    }

    private void configureViewModel() {
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        stepSummaryViewModel = ViewModelProviders.of(this, viewModelFactory).get(StepSummaryViewModel.class);
    }

    @Override
    protected void onServiceBound(final ShoeService.ShoeBinder binder) {

    }

    @Override
    protected void onServiceUnbound() {

    }

    @Override
    protected Class<? extends BleMulticonnectProfileService> getServiceClass() {
        return null;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

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
    protected int getAboutTextId() {
        return 0;
    }

    @Override
    protected UUID getFilterUUID() {
        return null;
    }

    @Override
    public void onDeviceConnected(@NonNull BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(@NonNull BluetoothDevice device) {

    }
}
