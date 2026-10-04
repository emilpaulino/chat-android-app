package com.pucmm.chatapp.data.model;

import java.util.Date;

public class Message {

    private String messageId;
    private String senderId;
    private String text;
    private Date timestamp;
    private String type;
    private String imageUrl;

    public Message(String messageId, String senderId, String text, Date timestamp, String type, String imageUrl) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.type = type;
        this.imageUrl = imageUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getType() {
        return type;
    }

}