package com.shamine.teamsmessagingbackend.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class BackupResource
{
    @Id
    @GeneratedValue
    private int resourceId;

    @Column(nullable = false)
    private String storageUrl;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date createdOn;

    @Column(nullable = false)
    private boolean available;

    public int getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(int resourceId)
    {
        this.resourceId = resourceId;
    }

    public String getStorageUrl()
    {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl)
    {
        this.storageUrl = storageUrl;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
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
}
