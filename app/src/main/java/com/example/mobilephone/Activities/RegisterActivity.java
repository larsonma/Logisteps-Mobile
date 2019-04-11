package com.example.mobilephone.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mobilephone.R;
import com.example.mobilephone.ViewModels.UserViewModel;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.AndroidInjection;

public class RegisterActivity extends AppCompatActivity {

    // UI reference
    private EditText mUsername;
    private EditText mPassword;
    private EditText mEmail;
    private EditText mLeftFoot;
    private EditText mRightFoot;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mHeight;
    private EditText mWeight;
    private EditText mStepGoal;
    private Button mCreate;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the Register Form
        mUsername = (EditText) findViewById(R.id.new_username);
        mPassword = (EditText) findViewById(R.id.new_password);
        mEmail = (EditText) findViewById(R.id.email);
        mLeftFoot = (EditText) findViewById(R.id.lfootsize);
        mRightFoot = (EditText) findViewById(R.id.rfootsize);
        mFirstName = (EditText) findViewById(R.id.f_name);
        mLastName = (EditText) findViewById(R.id.l_name);
        mHeight = (EditText) findViewById(R.id.height);
        mWeight = (EditText) findViewById(R.id.weight);
        mStepGoal = (EditText) findViewById(R.id.s_goal);
        mCreate = (Button) findViewById(R.id.create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempRegister();
            }
        });

        this.configureDagger();
        this.configureViewModel();
    }

    private void configureDagger() {
        AndroidInjection.inject(this);
    }

    private void configureViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
    }

    private void attempRegister() {
        // Reset errors.
        mUsername.setError(null);
        mPassword.setError(null);
        mEmail.setError(null);
        mLeftFoot.setError(null);
        mRightFoot.setError(null);
        mFirstName.setError(null);
        mLastName.setError(null);
        mHeight.setError(null);
        mWeight.setError(null);
        mStepGoal.setError(null);

        // Store values at time of the register attempt.
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        String email = mEmail.getText().toString();
        float lFootSize = Float.parseFloat(mLeftFoot.getText().toString());
        float rFootSize = Float.parseFloat(mRightFoot.getText().toString());
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        int height = Integer.parseInt(mHeight.getText().toString());
        int weight = Integer.parseInt(mWeight.getText().toString());
        int stepGoal = Integer.parseInt(mStepGoal.getText().toString());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        } else if (password.length() < 4) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email
        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        // Check for a valid left shoe size
        if (lFootSize < 4 || lFootSize > 20) {
            mLeftFoot.setError(getString(R.string.error_shoe_size));
            focusView = mLeftFoot;
            cancel = true;
        }

        if (rFootSize < 4 || rFootSize > 20) {
            mRightFoot.setError(getString(R.string.error_shoe_size));
            focusView = mRightFoot;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstName) || firstName.length() > 30) {
            mFirstName.setError(getString(R.string.error_first_name));
            focusView = mFirstName;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName) || lastName.length() > 30) {
            mLastName.setError(getString(R.string.error_last_name));
            focusView = mLastName;
            cancel = true;
        }

        if (height <= 0 || height > 120) {
            mHeight.setError(getString(R.string.error_height));
            focusView = mHeight;
            cancel = true;
        }

        if (weight <= 0 || weight > 1000) {
            mWeight.setError(getString(R.string.error_height));
            focusView = mWeight;
            cancel = true;
        }

        if (stepGoal <= 0 || stepGoal > 100000) {
            mStepGoal.setError(getString(R.string.error_step_goal));
            focusView = mStepGoal;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            viewModel.createUser(username, password, email, firstName, lastName, lFootSize, rFootSize,
                    height, weight, stepGoal, status -> {
                if(status == 201) {
                    startMainIntent(username, password);
                } else {
                    RegisterActivity.this.mUsername.setError(getString(R.string.error_user_taken));
                    RegisterActivity.this.mUsername.requestFocus();
                }
            });
        }
    }

    private void startMainIntent(String username, String password) {
        SharedPreferences userSession = getSharedPreferences("userCredentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = userSession.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();

        Intent mainActivityIntent = new Intent(RegisterActivity.this, MainActivity.class);
        RegisterActivity.this.startActivity(mainActivityIntent);

        finish();
    }

}
