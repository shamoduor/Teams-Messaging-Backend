package com.shamine.teamsmessagingbackend.models;

public class FCMMessage {
    private String to;
    private FCMData data;

    public FCMMessage(String to, FCMData data) {
        this.to = to;
        this.data = data;
    }

    public FCMMessage() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public FCMData getData() {
        return data;
    }

    public void setData(FCMData data) {
        this.data = data;
    }
}
