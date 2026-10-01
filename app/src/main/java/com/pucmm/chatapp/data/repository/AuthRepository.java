package com.pucmm.chatapp.data.repository;

import com.google.firebase.auth.FirebaseAuth;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Interfaz para comunicar el resultado del registro al ViewModel.
    public interface AuthCallback {
        void onSuccess();

        void onError(String error);
    }

    public void register(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onError(obtenerError(task.getException()));
            }
        });
    }

    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onError(obtenerError(task.getException()));
            }
        });
    }

    private String obtenerError(Exception exception) {
        if (exception != null) {
            return exception.getMessage();
        } else {
            return "Error desconocido";
        }
    }

}
