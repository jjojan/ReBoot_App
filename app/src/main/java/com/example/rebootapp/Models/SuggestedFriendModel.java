package com.example.rebootapp.Models;

public class SuggestedFriendModel {
    private String username;
    private String profilePicUrl;
    private String objectId;
    private int mutualFriends;

    public SuggestedFriendModel(String username, String profilePicUrl, String objectId, int mutualFriends) {
        this.username = username;
        this.profilePicUrl = profilePicUrl;
        this.objectId = objectId;
        this.mutualFriends = mutualFriends;
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

    public int getMutualFriends() {return mutualFriends;}
}