package com.pucmm.chatapp.viewmodel;

import android.net.Uri;

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

    public void getUsers(String currentUserId, UserRepository.UsersCallback callback) {
        userRepository.getUsers(currentUserId, callback);
    }

    public void updateFcmToken(String userId, UserRepository.TokenCallback callback) {
        userRepository.updateFcmToken(userId, callback);
    }

    public void removeFcmToken(String userId, UserRepository.TokenCallback callback) {
        userRepository.removeFcmToken(userId, callback);
    }

    public void updateUserName(String userId, String userName, UserRepository.TokenCallback callback) {
        userRepository.updateUserName(userId, userName, callback);
    }

    public void updateProfileImage(String userId, Uri imageUri, UserRepository.TokenCallback callback) {
        userRepository.updateProfileImage(userId, imageUri, callback);
    }
}
