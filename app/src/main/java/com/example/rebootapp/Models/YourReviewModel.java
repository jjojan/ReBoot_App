package com.example.rebootapp.Models;

import android.util.Log;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;

public class YourReviewModel {

    String ReviewUser;

    String ReviewUserName;
    String ReviewText;
    String GameID;
    String objectId;
    String photo_url;
    Date createdAt;
    Date updatedAt;
    boolean isShowOnlyFriends;
    float ratingStar;
    int upCount;
    int downCount;

    public YourReviewModel(String reviewUser, String reviewUserName, String reviewText, String gameID,String objectId
            , Date createdAt, Date updatedAt, boolean isShowOnlyFriends, float ratingStar,
                       int upCount,int downCount) {
        ReviewUser = reviewUser;
        ReviewUserName = reviewUserName;
        ReviewText = reviewText;
        GameID = gameID;
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isShowOnlyFriends = isShowOnlyFriends;
        this.ratingStar = ratingStar;
        this.upCount = upCount;
        this.downCount = downCount;
    }

    public YourReviewModel(String reviewUser, String reviewUserName, String reviewText, String gameID,String objectId
            , Date createdAt, Date updatedAt, boolean isShowOnlyFriends, float ratingStar,
                       int upCount,int downCount, String url) {
        ReviewUser = reviewUser;
        ReviewUserName = reviewUserName;
        ReviewText = reviewText;
        GameID = gameID;
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isShowOnlyFriends = isShowOnlyFriends;
        this.ratingStar = ratingStar;
        this.upCount = upCount;
        this.downCount = downCount;
        this.photo_url = url;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getUpCount() {
        return upCount;
    }

    public void setUpCount(int upCount) {
        this.upCount = upCount;
    }

    public int getDownCount() {
        return downCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
    }

    public String getReviewUser() {
        return ReviewUser;
    }

    public void setReviewUser(String reviewUser) {
        ReviewUser = reviewUser;
    }

    public String getReviewUserName() {
        return ReviewUserName;
    }

    public void setReviewUserName(String reviewUserName) {
        ReviewUserName = reviewUserName;
    }

    public String getReviewText() {
        return ReviewText;
    }

    public void setReviewText(String reviewText) {
        ReviewText = reviewText;
    }

    public String getGameID() {
        return GameID;
    }

    public void setGameID(String gameID) {
        GameID = gameID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isShowOnlyFriends() {
        return isShowOnlyFriends;
    }

    public void setShowOnlyFriends(boolean showOnlyFriends) {
        isShowOnlyFriends = showOnlyFriends;
    }

    public float getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(float ratingStar) {
        this.ratingStar = ratingStar;
    }

    public String getPhoto_url(){
        return photo_url;
    }

    public void delete( ){

    }
}
