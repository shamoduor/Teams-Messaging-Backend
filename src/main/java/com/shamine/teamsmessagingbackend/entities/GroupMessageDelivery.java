package com.shamine.teamsmessagingbackend.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class GroupMessageDelivery
{
    @Id
    @GeneratedValue
    private int deliveryId;

    @ManyToOne
    @JoinColumn(name = "message", nullable = false)
    private MessageGroup message;

    @ManyToOne
    @JoinColumn(name = "recipient", nullable = false)
    private ChatGroupMember recipient;

    private Date receivedOn;

    private Date readOn;

    public int getDeliveryId()
    {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId)
    {
        this.deliveryId = deliveryId;
    }

    public MessageGroup getMessage()
    {
        return message;
    }

    public void setMessage(MessageGroup message)
    {
        this.message = message;
    }

    public ChatGroupMember getRecipient()
    {
        return recipient;
    }

    public void setRecipient(ChatGroupMember recipient)
    {
        this.recipient = recipient;
    }

    public Date getReceivedOn()
    {
        return receivedOn;
    }

    public void setReceivedOn(Date receivedOn)
    {
        this.receivedOn = receivedOn;
    }

    public Date getReadOn()
    {
        return readOn;
    }

    public void setReadOn(Date readOn)
    {
        this.readOn = readOn;
    }
}
