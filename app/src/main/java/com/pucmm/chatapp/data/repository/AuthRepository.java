package com.pucmm.chatapp.data.repository;

import com.google.firebase.auth.FirebaseAuth;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;

    public AuthRepository(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Interfaz para comunicar el resultado del registro al ViewModel.
    public interface AuthCallback {
        void onSuccess();
        void onError(String error);
    }

    public void register(String email, String password, AuthCallback callback){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String error;

                        if (task.getException() != null) {
                            error = task.getException().getMessage();
                        } else {
                            error = "Error desconocido";
                        }

                        callback.onError(error);
                    }

                });
    }

}
