package com.pucmm.chatapp.viewmodel;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;
import com.pucmm.chatapp.data.model.Chat;
import com.pucmm.chatapp.data.model.Message;
import com.pucmm.chatapp.data.repository.ChatRepository;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;

    public ChatViewModel() {
        chatRepository = new ChatRepository();
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

    public interface ImageCallback {
        void onSuccess();
        void onError(String error);
    }

    public String generateChatId(String userId1, String userId2) {
        return chatRepository.generateChatId(userId1, userId2);
    }

    public void sendMessage(String chatId, String senderId, String receiverId, String text, MessageCallback callback) {

        chatRepository.sendMessage(chatId, senderId, receiverId, text, new ChatRepository.MessageCallback() {

            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void sendImage(String chatId, String senderId, String receiverId, Uri imageUri, ImageCallback callback) {

        chatRepository.sendImage(chatId, senderId, receiverId, imageUri, new ChatRepository.ImageCallback() {

            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public ListenerRegistration listenMessages(String chatId, MessagesCallback callback) {
        return chatRepository.listenMessages(chatId, new ChatRepository.MessagesCallback() {
            @Override
            public void onSuccess(List<Message> messages) {
                callback.onSuccess(messages);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public ListenerRegistration listenChats(String currentUserId, ChatsCallback callback) {

        return chatRepository.listenChats(currentUserId, new ChatRepository.ChatsCallback() {

            @Override
            public void onSuccess(List<Chat> chats) {
                callback.onSuccess(chats);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
}