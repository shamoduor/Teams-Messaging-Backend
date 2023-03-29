package com.shamine.teamsmessagingbackend.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="message_private")
public class  MessagePrivate extends Message
{
    @ManyToOne
    @JoinColumn(name = "recipient", nullable = false)
    private User recipient;

    @Column(nullable = false)
    private boolean availableForSender;

    @Column(nullable = false)
    private boolean availableForRecipient;

    private Date receivedOn;

    private Date readOn;

    public User getRecipient()
    {
        return recipient;
    }

    public void setRecipient(User recipient)
    {
        this.recipient = recipient;
    }

    public boolean isAvailableForSender()
    {
        return availableForSender;
    }

    public void setAvailableForSender(boolean availableForSender)
    {
        this.availableForSender = availableForSender;
    }

    public boolean isAvailableForRecipient()
    {
        return availableForRecipient;
    }

    public void setAvailableForRecipient(boolean availableForRecipient)
    {
        this.availableForRecipient = availableForRecipient;
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
