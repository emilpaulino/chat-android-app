package com.pucmm.chatapp.data.model;

import java.util.Date;

public class Message {
    private String messageId;
    private String senderId;
    private String text;
    private Date timestamp;

    public Message(String messageId, String senderId, String text, Date timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
