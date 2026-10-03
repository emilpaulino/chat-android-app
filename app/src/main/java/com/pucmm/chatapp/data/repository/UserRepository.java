package com.pucmm.chatapp.data.repository;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucmm.chatapp.data.model.User;

public class UserRepository {
    private final FirebaseFirestore db;

    public UserRepository(){
        db = FirebaseFirestore.getInstance("chat-app");
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public void getUser(String userId, UserCallback callback) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String userName = document.getString("userName");
                        String email = document.getString("email");
                        String profileImage = document.getString("profileImage");
                        User user = new User(document.getId(), userName, email, profileImage);
                        callback.onSuccess(user);
                    } else {
                        callback.onError("Usuario no encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }


}
