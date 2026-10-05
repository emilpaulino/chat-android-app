package com.pucmm.chatapp.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance("chat-app");
    }

    public interface AuthCallback {
        void onSuccess();

        void onError(String error);
    }

    public void register(String name, String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

                firebaseAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {

                    if (profileTask.isSuccessful()) {
                        saveUserOnFirestore(firebaseAuth.getCurrentUser(), name, email, callback);
                    } else {
                        callback.onError("No se pudo guardar el nombre");
                    }

                });
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

    private void saveUserOnFirestore(FirebaseUser user, String name, String email, AuthCallback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("userName", name);
        userData.put("email", email);
        userData.put("profileImage", "");

        db.collection("users")
                .document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void logout() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        db.collection("users")
                .document(user.getUid())
                .update("fcmToken", FieldValue.delete())
                .addOnCompleteListener(task -> firebaseAuth.signOut());
    }

    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

}
