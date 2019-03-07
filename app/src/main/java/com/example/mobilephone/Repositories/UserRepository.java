package com.example.mobilephone.Repositories;

import android.util.Log;

import com.example.mobilephone.Databases.UserDao;
import com.example.mobilephone.Models.User;
import com.example.mobilephone.Services.LogistepsService;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    public LiveData<User> getLiveUser(String username, String password) {
        refreshUser(username, password);
        return userDao.loadLive(username);
    }

    public User getUser(String username, String password) throws ExecutionException, InterruptedException {
        Callable<User> callable = () -> {
            refreshUserSync(username, password);
            return userDao.load(username);
        };

        Future<User> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    private void createNewUser(User user, Consumer<Integer> consumer) {
        executor.execute(() -> {
            try {
                Response<User> response = webservice.postUser(user).execute();
                userDao.save(user);
                consumer.accept(response.code());
            } catch (IOException e) {
                // TODO: check for errors
                Log.e(TAG, e.toString());
            }
        });
    }

    private void refreshUserSync(final String username, final String password) {
        User userExists = userDao.hasUser(username, getMaxRefreshTime(new Date()));
        if (userExists == null && password != null) {
            try {
                Response<User> response = webservice.getUser(username,
                        Credentials.basic(username, password))
                        .execute();
                if (response.code() == 200) {
                    User user = response.body();
                    user.getBaseUser().setPassword(password);

                    userDao.save(user);
                }
            } catch (IOException e) {
                // TODO: check for errors
            }
        }
    }

    private void refreshUser(final String username, final String password) {
        executor.execute(() -> {
            User userExists = userDao.hasUser(username, getMaxRefreshTime(new Date()));
            if (userExists == null && password != null) {
                try {
                    Response<User> response = webservice.getUser(username,
                            Credentials.basic(username, password))
                            .execute();
                    if (response.code() == 200) {
                        User user = response.body();
                        user.getBaseUser().setPassword(password);

                        userDao.save(user);
                    }
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
