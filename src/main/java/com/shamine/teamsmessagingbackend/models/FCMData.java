package com.shamine.teamsmessagingbackend.models;

public class FCMData {
    private String picUrl;
    private String title;
    private String message;
    private String createdOn;
    private String sender;
    private String receiver;

    public FCMData(String picUrl, String title, String message, String createdOn, String sender, String receiver) {
        this.picUrl = picUrl;
        this.title = title;
        this.message = message;
        this.createdOn = createdOn;
        this.receiver = receiver;
        this.sender = sender;
    }

    public FCMData() {
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
