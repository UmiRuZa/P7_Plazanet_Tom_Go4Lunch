package com.project.go4lunch.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.project.go4lunch.model.restaurant.Result;

import java.util.List;

public class User {

    private String uid;
    private String username;
    private String urlPicture;
    private String userMail;

    private String selectRestID;
    private String selectRestName;
    private String selectRestAddress;

    private LatLng restLatLng;
    private boolean isSearch;
    private int newRadius;
    private boolean isNotify;

    private List<String> restLikedList;

    public User() {
    }

    public User(String uid, String username, @Nullable String urlPicture, String userMail,
                String selectRestID, String selectRestName, String selectRestAddress, Boolean isNotify, List<String> restLikedList) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.userMail = userMail;
        this.selectRestID = selectRestID;
        this.selectRestName = selectRestName;
        this.selectRestAddress = selectRestAddress;
        this.isNotify = isNotify;
        this.restLikedList = restLikedList;
    }

    // --- GETTERS ---

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public String getUserMail() {
        return userMail;
    }

    public String getSelectRestID() {
        return selectRestID;
    }

    public String getSelectRestName() {
        return selectRestName;
    }

    public String getSelectRestAddress() {
        return selectRestAddress;
    }

    public List<String> getRestLikedList() {
        return restLikedList;
    }

    // --- SETTERS ---

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public void setRestLikedList(List<String> restLikedList) {
        this.restLikedList = restLikedList;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSelectRestID(String selectRestID) {
        this.selectRestID = selectRestID;
    }

    public void setSelectRestName(String selectRestName) {
        this.selectRestName = selectRestName;
    }

    public void setSelectRestAddress(String selectRestAddress) {
        this.selectRestAddress = selectRestAddress;
    }

}
