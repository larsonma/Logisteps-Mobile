package com.example.mobilephone.Repositories;

import android.os.Debug;
import android.util.Log;

import com.example.mobilephone.Databases.UserDao;
import com.example.mobilephone.Models.StepSummary;
import com.example.mobilephone.Models.User;
import com.example.mobilephone.Services.LogistepsService;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import okhttp3.Credentials;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

@Singleton
public class UserRepository {
    private static int FRESH_TIMEOUT_IN_MINUTES = 5;

    private final LogistepsService webservice;
    private final UserDao userDao;
    private final Executor executor;

    @Inject
    public UserRepository(LogistepsService webservice, UserDao userDao, Executor executor) {
        this.webservice = webservice;
        this.userDao = userDao;
        this.executor = executor;
    }

    public void createUser(User user, Consumer<Integer> consumer) {
        createNewUser(user, consumer);
    }

    public LiveData<User> getUser(User user) {
        refreshUser(user);
        return userDao.load(user.getId());
    }

    private void createNewUser(User user, Consumer<Integer> consumer) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<User> response = webservice.postUser(user).execute();
                    userDao.save(user);
                    consumer.accept(response.code());
                } catch (IOException e) {
                    // TODO: check for errors
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    private void refreshUser(final User user) {
        executor.execute(() -> {
            User userExists = userDao.hasUser(user.getId(), getMaxRefreshTime(new Date()));
            if (userExists == null) {
                try {
                    Response<User> response = webservice.getUser(user.getBaseUser().getUsername(),
                            Credentials.basic(user.getBaseUser().getUsername(), user.getBaseUser().getPassword()))
                            .execute();
                    userDao.save(response.body());
                } catch (IOException e) {
                    // TODO: check for errors
                }
            }
        });
    }

    private Date getMaxRefreshTime(Date currentDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MINUTE, -FRESH_TIMEOUT_IN_MINUTES);
        return cal.getTime();
    }
}
