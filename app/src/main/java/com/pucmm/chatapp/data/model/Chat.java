package com.pucmm.chatapp.data.model;

public class Chat {
    private String chatID;
    private User user;
    private Message lastMessage;

    public Chat(String chatID, User user, Message lastMessage) {
        this.chatID = chatID;
        this.user = user;
        this.lastMessage = lastMessage;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
