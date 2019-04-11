package com.example.mobilephone.ViewModels;

import com.example.mobilephone.Models.BaseUser;
import com.example.mobilephone.Models.Shoe;
import com.example.mobilephone.Models.User;
import com.example.mobilephone.Repositories.UserRepository;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private LiveData<User> liveUser;
    private User user;
    private UserRepository userRepository;

    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void init(String username, String password) {
        if (this.liveUser != null) {
            return;
        }
        this.liveUser = userRepository.getLiveUser(username, password);
        try {
           this.user = userRepository.getUser(username, password);
        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        }
    }

    public void createUser(String username, String password, String email, String firstName,
            String lastName, float lFootSize, float rFootSize, int height, int weight,
            int stepGoal, Consumer<Integer> consumer) {
        Shoe leftFoot = new Shoe("L", lFootSize);
        Shoe rightFoot = new Shoe("R", rFootSize);
        BaseUser baseUser = new BaseUser(username, password, email, firstName, lastName);
        User user = new User(baseUser, leftFoot, rightFoot, height, weight, stepGoal);

        userRepository.createUser(user, consumer);
    }

    public LiveData<User> getLiveUser() {
        return this.liveUser;
    }

    public User getUser() {
        return this.user;
    }

    public boolean authenticateUser(String username, String password) {
        boolean result = false;
        try {
            User user = userRepository.getUser(username, password);
            this.user = user;

            result = user != null && username.equals(user.getBaseUser().getUsername())
                    && password.equals(user.getBaseUser().getPassword());
        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        }
        return result;
    }
}
