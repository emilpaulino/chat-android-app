package com.pucmm.chatapp.data.model;

public class User {
    private String userId;
    private String userName;
    private String email;
    private String profileImage;

    public User(String userId, String userName, String email, String profileImage) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.profileImage = profileImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
