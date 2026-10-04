package com.pucmm.chatapp.data.repository;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.pucmm.chatapp.data.model.Chat;
import com.pucmm.chatapp.data.model.Message;
import com.pucmm.chatapp.data.model.User;

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

    public interface ChatsCallback {
        void onSuccess(List<Chat> chats);

        void onError(String error);
    }

    public void sendMessage(String chatId, String senderId, String receiverId, String text, MessageCallback callback) {

        String messageId = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document()
                .getId();

        Map<String, Object> messageData = new HashMap<>();

        messageData.put("messageId", messageId);
        messageData.put("senderId", senderId);
        messageData.put("text", text);
        messageData.put("timestamp", new Date());

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(messageData)
                .addOnSuccessListener(aVoid -> {

                    Map<String, Object> chatData = new HashMap<>();

                    List<String> participants = new ArrayList<>();
                    participants.add(senderId);
                    participants.add(receiverId);

                    chatData.put("participants", participants);
                    chatData.put("lastMessage", text);
                    chatData.put("lastMessageSenderId", senderId);
                    chatData.put("lastMessageTimestamp", new Date());

                    db.collection("chats")
                            .document(chatId)
                            .set(chatData)
                            .addOnSuccessListener(aVoid2 -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
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

    public ListenerRegistration listenChats(String currentUserId, ChatsCallback callback) {

        return db.collection("chats").whereArrayContains("participants", currentUserId).addSnapshotListener((querySnapshot, error) -> {

            if (error != null) {
                callback.onError(error.getMessage());
                return;
            }

            if (querySnapshot == null) {
                callback.onSuccess(new ArrayList<>());
                return;
            }

            List<Chat> chats = new ArrayList<>();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                String chatId = document.getId();
                List<String> participants = (List<String>) document.get("participants");
                String otherUserId = null;

                if (participants != null) {
                    for (String participantId : participants) {
                        if (!participantId.equals(currentUserId)) {
                            otherUserId = participantId;
                            break;
                        }
                    }
                }

                if (otherUserId == null) {
                    continue;
                }

                String lastMessageText = document.getString("lastMessage");
                String lastMessageSenderId = document.getString("lastMessageSenderId");
                Timestamp timestamp = document.getTimestamp("lastMessageTimestamp");
                Date lastMessageDate = null;

                if (timestamp != null) {
                    lastMessageDate = timestamp.toDate();
                }

                Message lastMessage = new Message("", lastMessageSenderId, lastMessageText, lastMessageDate);
                String finalOtherUserId = otherUserId;

                UserRepository userRepository = new UserRepository();
                userRepository.getUser(finalOtherUserId, new UserRepository.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Chat chat = new Chat(chatId, user, lastMessage);
                        chats.add(chat);
                        if (chats.size() == querySnapshot.size()) {
                            chats.sort((chat1, chat2) -> {
                                Date date1 = chat1.getLastMessage().getTimestamp();
                                Date date2 = chat2.getLastMessage().getTimestamp();
                                if (date1 == null && date2 == null) {
                                    return 0;
                                }
                                if (date1 == null) {
                                    return 1;
                                }
                                if (date2 == null) {
                                    return -1;
                                }
                                return date2.compareTo(date1);
                            });
                            callback.onSuccess(chats);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }

            if (querySnapshot.isEmpty()) {
                callback.onSuccess(chats);
            }
        });
    }

}