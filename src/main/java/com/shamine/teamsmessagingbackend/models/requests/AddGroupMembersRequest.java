package com.shamine.teamsmessagingbackend.models.requests;

public class AddGroupMembersRequest extends CreateGroupRequest
{
    private int groupId;

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }
}
