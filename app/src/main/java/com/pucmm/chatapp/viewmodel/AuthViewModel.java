package com.pucmm.chatapp.viewmodel;

import androidx.lifecycle.ViewModel;

import com.pucmm.chatapp.data.repository.AuthRepository;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    public void register(String email, String password, AuthRepository.AuthCallback callback) {
        authRepository.register(email, password, callback);
    }

    public void login(String email, String password, AuthRepository.AuthCallback callback){
        authRepository.login(email, password, callback);
    }

}
