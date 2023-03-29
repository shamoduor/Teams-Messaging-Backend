package com.shamine.teamsmessagingbackend.models.requests;

public class MessageGroupRequest extends MessageRequest
{
    private int chatGroupId;
    private String senderName;

    public int getChatGroupId()
    {
        return chatGroupId;
    }

    public void setChatGroupId(int chatGroupId)
    {
        this.chatGroupId = chatGroupId;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    @Override
    public String toString()
    {
        return "MessagePrivateRequest{" +
                "messageId=" + getMessageId() +
                ", content='" + getContent() + '\'' +
                ", createdOn=" + getCreatedOn() +
                ", senderId=" + getSenderId() +
                ", recipientId=" + getChatGroupId() +
                '}';
    }
}
