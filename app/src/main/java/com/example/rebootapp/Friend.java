package com.example.rebootapp;

public class Friend {
    private String username;
    private String profilePicUrl;
    private String objectId;

    public Friend(String username, String profilePicUrl, String objectId) {
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
