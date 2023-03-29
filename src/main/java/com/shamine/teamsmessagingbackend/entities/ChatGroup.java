package com.shamine.teamsmessagingbackend.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class ChatGroup
{
    @Id
    @GeneratedValue
    private int chatGroupId;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private Date createdOn;

    @Column(nullable = false)
    private boolean available;

    private String picUrl;

    @OneToMany(mappedBy = "chatGroup")
    private List<ChatGroupMember> chatGroupMembers;

    @OneToMany(mappedBy = "chatGroup")
    private List<MessageGroup> groupMessages;

    public int getChatGroupId()
    {
        return chatGroupId;
    }

    public void setChatGroupId(int chatGroupId)
    {
        this.chatGroupId = chatGroupId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public User getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(User createdBy)
    {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn)
    {
        this.createdOn = createdOn;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public List<ChatGroupMember> getChatGroupMembers()
    {
        return chatGroupMembers;
    }

    public void setChatGroupMembers(List<ChatGroupMember> chatGroupMembers)
    {
        this.chatGroupMembers = chatGroupMembers;
    }

    public List<MessageGroup> getGroupMessages()
    {
        return groupMessages;
    }

    public void setGroupMessages(List<MessageGroup> groupMessages)
    {
        this.groupMessages = groupMessages;
    }

    public String getPicUrl()
    {
        return picUrl;
    }

    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }
}
