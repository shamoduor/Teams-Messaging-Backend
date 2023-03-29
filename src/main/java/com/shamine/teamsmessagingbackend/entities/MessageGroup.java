package com.shamine.teamsmessagingbackend.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="message_group")
public class MessageGroup extends Message
{
    @ManyToOne
    @JoinColumn(name = "chat_group", nullable = false)
    private ChatGroup chatGroup;

    @OneToMany(mappedBy = "message")
    private List<GroupMessageDelivery> deliveries;

    public ChatGroup getChatGroup()
    {
        return chatGroup;
    }

    public void setChatGroup(ChatGroup chatGroup)
    {
        this.chatGroup = chatGroup;
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
