package com.pucmm.chatapp.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pucmm.chatapp.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance("chat-app");
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface UsersCallback {
        void onSuccess(List<User> users);
        void onError(String error);
    }

    public interface TokenCallback {
        void onSuccess();
        void onError(String error);
    }

    public void getUser(String userId, UserCallback callback) {
        db.collection("users").document(userId).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                String userName = document.getString("userName");
                String email = document.getString("email");
                String profileImage = document.getString("profileImage");
                User user = new User(document.getId(), userName, email, profileImage);
                callback.onSuccess(user);
            } else {
                callback.onError("Usuario no encontrado");
            }
        }).addOnFailureListener(e -> {
            callback.onError(e.getMessage());
        });
    }

    public void getUsers(String currentUserId, UsersCallback callback) {
        db.collection("users").get().addOnSuccessListener(querySnapshot -> {

            List<User> users = new ArrayList<>();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                if(document.getId().equals(currentUserId)){
                    continue;
                }
                String userName = document.getString("userName");
                String email = document.getString("email");
                String profileImage = document.getString("profileImage");
                User user = new User(document.getId(), userName, email, profileImage);
                users.add(user);
            }

            callback.onSuccess(users);

        }).addOnFailureListener(e -> {
            callback.onError(e.getMessage());
        });
    }

    public void updateFcmToken(String userId, TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                callback.onError("Error al obtener el token");
                return;
            }

            String token = task.getResult();

            db.collection("users")
                    .document(userId)
                    .update("fcmToken", token)
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        });
    }

    public void removeFcmToken(String userId, TokenCallback callback) {
        db.collection("users")
                .document(userId)
                .update("fcmToken", com.google.firebase.firestore.FieldValue.delete())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

}
