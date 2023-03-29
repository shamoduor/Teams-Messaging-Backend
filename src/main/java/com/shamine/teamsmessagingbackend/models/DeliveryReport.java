package com.shamine.teamsmessagingbackend.models;

import java.util.Date;

public class DeliveryReport {

    private final int sender;
    private final int messageId;
    private final Date receivedOn;

    public DeliveryReport(int sender, int messageId, long receivedOn) {
        this.sender = sender;
        this.messageId = messageId;
        this.receivedOn = new Date(receivedOn);
    }

    public int getSender() {
        return sender;
    }

    public int getMessageId() {
        return messageId;
    }

    public Date getReceivedOn() {
        return receivedOn;
    }
}
