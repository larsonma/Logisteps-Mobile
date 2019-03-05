package com.example.mobilephone.Activities;

import androidx.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilephone.R;
import com.example.mobilephone.ViewModels.StepSummaryViewModel;

public class MainActivity extends AppCompatActivity {

    /**
     * Async Task for fetching user data step data for the day.
     */
    private StepsSummaryTask summaryTask = null;

    // UI References
    private TextView mStepsTaken;
    private TextView mStepsPerHr;
    private TextView mProjectedSteps;
    private TextView mLShoeStatus;
    private TextView mRShoeStatus;

    private StepSummaryViewModel viewModel;

    String username = null;
    String password = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the UI
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

        setUserCredentials();
        viewModel.getStepSummary().observe(this, stepSummary -> {
            // TODO: update UI
        });
    }

    private void setUserCredentials() {
        SharedPreferences userCredentials = getSharedPreferences("Login", MODE_PRIVATE);

        username = userCredentials.getString("Username", null);
        password = userCredentials.getString("Password", null);
    }

    public class StepsSummaryTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return false;
        }
    }
}
