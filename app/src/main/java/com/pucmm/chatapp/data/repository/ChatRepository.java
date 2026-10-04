package com.pucmm.chatapp.data.repository;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.pucmm.chatapp.data.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository {

    private final FirebaseFirestore db;

    public ChatRepository() {
        db = FirebaseFirestore.getInstance("chat-app");
    }

    public String generateChatId(String userId1, String userId2) {

        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public interface MessageCallback {
        void onSuccess();

        void onError(String error);
    }

    public interface MessagesCallback {
        void onSuccess(List<Message> messages);

        void onError(String error);
    }

    public void sendMessage(String chatId, String senderId, String text, MessageCallback callback) {

        String messageId = db.collection("chats").document(chatId).collection("messages").document().getId();

        Map<String, Object> messageData = new HashMap<>();

        messageData.put("messageId", messageId);
        messageData.put("senderId", senderId);
        messageData.put("text", text);
        messageData.put("timestamp", new Date());

        db.collection("chats").document(chatId).collection("messages").document(messageId).set(messageData).addOnSuccessListener(aVoid -> callback.onSuccess()).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public ListenerRegistration listenMessages(String chatId, MessagesCallback callback) {

        return db.collection("chats").document(chatId).collection("messages").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener((querySnapshot, error) -> {

            if (error != null) {
                callback.onError(error.getMessage());
                return;
            }

            List<Message> messages = new ArrayList<>();

            if (querySnapshot != null) {

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                    String messageId = document.getString("messageId");
                    String senderId = document.getString("senderId");
                    String text = document.getString("text");

                    Timestamp timestamp = document.getTimestamp("timestamp");

                    Date date = null;

                    if (timestamp != null) {
                        date = timestamp.toDate();
                    }

                    Message message = new Message(messageId, senderId, text, date);

                    messages.add(message);
                }
            }

            callback.onSuccess(messages);
        });
    }
}