package com.shamine.teamsmessagingbackend.models.requests;

public class RenameGroupRequest
{
    private String title;
    private int groupId;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }
}
