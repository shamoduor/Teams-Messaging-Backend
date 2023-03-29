package com.shamine.teamsmessagingbackend.models.requests;

public class MessageRequest
{
    private int messageId;
    private String content;
    private long createdOn;
    private int senderId;

    public int getMessageId()
    {
        return messageId;
    }

    public void setMessageId(int messageId)
    {
        this.messageId = messageId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public long getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(long createdOn)
    {
        this.createdOn = createdOn;
    }

    public int getSenderId()
    {
        return senderId;
    }

    public void setSenderId(int senderId)
    {
        this.senderId = senderId;
    }
}
