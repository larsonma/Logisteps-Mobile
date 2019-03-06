package com.example.mobilephone.ViewModels;

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

    public void init(User user) {
        if (this.user != null) {
            return;
        }
        this.user = userRepository.getUser(user);
    }

    public void createUser(User user, Consumer<Integer> consumer) {
        userRepository.createUser(user, consumer);
    }

    public LiveData<User> getUser() { return this.user; }
}
