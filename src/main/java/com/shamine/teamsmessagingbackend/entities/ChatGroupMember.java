package com.shamine.teamsmessagingbackend.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class ChatGroupMember
{
    @Id
    @GeneratedValue
    private int memberId;

    @ManyToOne
    @JoinColumn(name = "chat_group", nullable = false)
    private ChatGroup chatGroup;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date addedOn;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable = false)
    private boolean isAdmin;

    @OneToMany(mappedBy = "recipient")
    private List<GroupMessageDelivery> deliveries;

    public int getMemberId()
    {
        return memberId;
    }

    public void setMemberId(int memberId)
    {
        this.memberId = memberId;
    }

    public ChatGroup getChatGroup()
    {
        return chatGroup;
    }

    public void setChatGroup(ChatGroup chatGroup)
    {
        this.chatGroup = chatGroup;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getAddedOn()
    {
        return addedOn;
    }

    public void setAddedOn(Date addedOn)
    {
        this.addedOn = addedOn;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(boolean admin)
    {
        isAdmin = admin;
    }

    public List<GroupMessageDelivery> getDeliveries()
    {
        return deliveries;
    }

    public void setDeliveries(List<GroupMessageDelivery> deliveries)
    {
        this.deliveries = deliveries;
    }
}
