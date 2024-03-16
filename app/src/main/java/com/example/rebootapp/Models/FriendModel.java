package com.example.rebootapp.Models;

public class FriendModel {
    private String username;
    private String profilePicUrl;
    private String objectId;

    public FriendModel(String username, String profilePicUrl, String objectId) {
        this.username = username;
        this.profilePicUrl = profilePicUrl;
        this.objectId = objectId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getObjectId() {
        return objectId;
    }
}
