package com.shamine.teamsmessagingbackend.models.requests;

public class MessagePrivateRequest extends MessageRequest
{
    private int recipientId;
    private boolean availableForRecipient;
    private Long receivedOn;
    private Long readOn;

    public int getRecipientId()
    {
        return recipientId;
    }

    public void setRecipientId(int recipientId)
    {
        this.recipientId = recipientId;
    }

    public boolean isAvailableForRecipient()
    {
        return availableForRecipient;
    }

    public void setAvailableForRecipient(boolean availableForRecipient)
    {
        this.availableForRecipient = availableForRecipient;
    }

    public Long getReceivedOn()
    {
        return receivedOn;
    }

    public void setReceivedOn(Long receivedOn)
    {
        this.receivedOn = receivedOn;
    }

    public Long getReadOn()
    {
        return readOn;
    }

    public void setReadOn(Long readOn)
    {
        this.readOn = readOn;
    }

    @Override
    public String toString()
    {
        return "MessagePrivateRequest{" +
                "messageId=" + getMessageId() +
                ", content='" + getContent() + '\'' +
                ", createdOn=" + getCreatedOn() +
                ", senderId=" + getSenderId() +
                ", recipientId=" + recipientId +
                ", availableForRecipient=" + availableForRecipient +
                ", receivedOn=" + receivedOn +
                '}';
    }
}
