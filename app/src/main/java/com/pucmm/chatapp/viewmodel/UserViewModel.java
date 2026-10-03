package com.pucmm.chatapp.viewmodel;

import androidx.lifecycle.ViewModel;

import com.pucmm.chatapp.data.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;

    public UserViewModel(){
        userRepository = new UserRepository();
    }

    public void getUser(String userId, UserRepository.UserCallback callback) {
        userRepository.getUser(userId, callback);
    }

    public void getUsers(UserRepository.UsersCallback callback) {
        userRepository.getUsers(callback);
    }
}
