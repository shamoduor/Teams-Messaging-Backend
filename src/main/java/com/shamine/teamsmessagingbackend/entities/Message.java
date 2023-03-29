package com.shamine.teamsmessagingbackend.entities;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class Message
{
    @Id
    @GeneratedValue
    private int messageId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    private User sender;

    @Column(nullable = false)
    private Date createdOn;

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

    public User getSender()
    {
        return sender;
    }

    public void setSender(User sender)
    {
        this.sender = sender;
    }

    public Date getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn)
    {
        this.createdOn = createdOn;
    }
}
