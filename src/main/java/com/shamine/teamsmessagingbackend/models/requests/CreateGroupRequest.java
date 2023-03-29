package com.shamine.teamsmessagingbackend.models.requests;

import java.util.List;

public class CreateGroupRequest
{
    private List<Integer> memberIds;
    private String groupTitle;

    public List<Integer> getMemberIds()
    {
        return memberIds;
    }

    public void setMemberIds(List<Integer> memberIds)
    {
        this.memberIds = memberIds;
    }

    public String getGroupTitle()
    {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle)
    {
        this.groupTitle = groupTitle;
    }
}
