package com.example.mobilephone.ViewModels;

import com.example.mobilephone.Models.BaseUser;
import com.example.mobilephone.Models.Shoe;
import com.example.mobilephone.Models.User;
import com.example.mobilephone.Repositories.UserRepository;

import java.util.function.Consumer;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private LiveData<User> user;
    private UserRepository userRepository;

    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void init(String username, String password) {
        if (this.user != null) {
            return;
        }
        this.user = userRepository.getUser(username, password);
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

    public LiveData<User> getUser() {
        return this.user;
    }
}
