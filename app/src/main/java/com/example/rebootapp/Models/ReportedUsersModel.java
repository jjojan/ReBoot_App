package com.example.rebootapp.Models;

public class ReportedUsersModel {
    private String username;
    private String profilePicUrl;
    private String objectId;
    private int mutualFriends;

    private int reportNum;

    public ReportedUsersModel(String username, String profilePicUrl, String objectId, int mutualFriends, int reportNum) {
        this.username = username;
        this.profilePicUrl = profilePicUrl;
        this.objectId = objectId;
        this.mutualFriends = mutualFriends;
        this.reportNum = reportNum;
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

    public int getreportNum(){ return reportNum;}
}
