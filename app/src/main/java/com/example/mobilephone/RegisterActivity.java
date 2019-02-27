package com.example.mobilephone;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private UserRegisterTask mRegisterTask = null;

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
    }

    private void attempRegister() {
        if (mRegisterTask != null) {
            return;
        }

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
            mRegisterTask = new UserRegisterTask(username, password, email, lFootSize, rFootSize,
                    firstName, lastName, height, weight, stepGoal);
            mRegisterTask.execute((Void) null);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private final String mEmail;
        private final float mLeftFootSize;
        private final float mRightFootSize;
        private final String mFirstName;
        private final String mLastName;
        private final int mHeight;
        private final int mWeight;
        private final int mStepGoal;

        private static final String webEndpoint = "http://10.0.2.2:8000/api/user/";

        UserRegisterTask(String username, String password, String email, float lFootSize, float rFootSize,
                         String firstName, String lastName, int height, int weight, int goal) {
            mUsername = username;
            mPassword = password;
            mEmail = email;
            mLeftFootSize = lFootSize;
            mRightFootSize = rFootSize;
            mFirstName = firstName;
            mLastName = lastName;
            mHeight = height;
            mWeight = weight;
            mStepGoal = goal;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int status;

            try {
                status = registerUser();
            } catch (IOException e) {
                return false;
            }

            return status == 201;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;

            if (success) {
                finish();
                Intent mainActivityIntent = new Intent(RegisterActivity.this, MainActivity.class);
                RegisterActivity.this.startActivity(mainActivityIntent);
            } else {
                RegisterActivity.this.mUsername.setError(getString(R.string.error_user_taken));
                RegisterActivity.this.mUsername.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
        }

        private int registerUser() throws IOException {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType,
                    "{\"user\":{" +
                            "\"username\":\"" + mUsername + "\"," +
                            "\"email\":\"" + mEmail + "\"," +
                            "\"first_name\":\"" + mFirstName + "\"," +
                            "\"last_name\":\"" + mLastName +"\"," +
                            "\"password\":\"" + mPassword + "\"}," +
                            "\"right_shoe\":{" +
                            "\"foot\":\"R\"," +
                            "\"size\":" + mRightFootSize + "}," +
                            "\"left_shoe\":{" +
                            "\"foot\":\"L\"," +
                            "\"size\":" + mLeftFootSize + "}," +
                            "\"height\":" + mHeight + "," +
                            "\"weight\":" + mWeight + "," +
                            "\"step_goal\":" + mStepGoal + "}");

            Request request = new Request.Builder()
                    .url(webEndpoint)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.code();
            }
        }
    }
}
