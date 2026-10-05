package com.pucmm.chatapp.viewmodel;

import androidx.lifecycle.ViewModel;

import com.pucmm.chatapp.data.repository.AuthRepository;
import com.pucmm.chatapp.data.repository.UserRepository;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    public AuthViewModel() {
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
    }

    public void register(String name, String email, String password, AuthRepository.AuthCallback callback) {
        authRepository.register(name, email, password, callback);
    }

    public void login(String email, String password, AuthRepository.AuthCallback callback){
        authRepository.login(email, password, callback);
    }

    public void logout(){
        authRepository.logout();
    }

    public String getCurrentUserId() {
        return authRepository.getCurrentUserId();
    }

}
