package com.example.rebootapp.Models;

import java.util.List;

public class UserListModel {
    String listName;
    List<String> gameName;
    List<String>  gamePreviewLink;
    List<String>  gameID;
    String userID;
    String objectID;

    public UserListModel(String listName, List<String> gameName, List<String> gamePreviewLink, List<String> gameID, String userID, String objectID) {
        this.listName = listName;
        this.gameName = gameName;
        this.gamePreviewLink = gamePreviewLink;
        this.gameID = gameID;
        this.userID = userID;
        this.objectID = objectID;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<String> getGameName() {
        return gameName;
    }

    public void setGameName(List<String> gameName) {
        this.gameName = gameName;
    }

    public List<String> getGamePreviewLink() {
        return gamePreviewLink;
    }

    public void setGamePreviewLink(List<String> gamePreviewLink) {
        this.gamePreviewLink = gamePreviewLink;
    }

    public List<String> getGameID() {
        return gameID;
    }

    public void setGameID(List<String> gameID) {
        this.gameID = gameID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }
}
