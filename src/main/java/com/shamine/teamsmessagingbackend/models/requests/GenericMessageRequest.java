package com.shamine.teamsmessagingbackend.models.requests;

import com.shamine.teamsmessagingbackend.models.DeliveryReport;

import java.util.HashMap;
import java.util.List;

public class GenericMessageRequest {

    private MessageGroupRequest messageGroup;
    private MessagePrivateRequest messagePrivate;
    private DeliveryReport deliveryReport;
    private GroupMessageDeliveryRequest groupMessageDelivery;
    private List<DeliveryReport> undeliveredReports;
    private List<GroupMessageDeliveryRequest> groupMessageDeliveries;
    private SyncRequest syncRequest;
    private HashMap<String, Object> contact;
    private HashMap<String, Object> chatGroup;
    private List<HashMap<String, Object>> chatGroupMembers;

    public MessagePrivateRequest getMessagePrivate() {
        return messagePrivate;
    }

    public void setMessagePrivate(MessagePrivateRequest messagePrivate) {
        this.messagePrivate = messagePrivate;
    }

    public MessageGroupRequest getMessageGroup() {
        return messageGroup;
    }

    public void setMessageGroup(MessageGroupRequest messageGroup) {
        this.messageGroup = messageGroup;
    }

    public DeliveryReport getDeliveryReport() {
        return deliveryReport;
    }

    public void setDeliveryReport(DeliveryReport deliveryReport) {
        this.deliveryReport = deliveryReport;
    }

    public GroupMessageDeliveryRequest getGroupMessageDelivery()
    {
        return groupMessageDelivery;
    }

    public void setGroupMessageDelivery(GroupMessageDeliveryRequest groupMessageDelivery)
    {
        this.groupMessageDelivery = groupMessageDelivery;
    }

    public List<DeliveryReport> getUndeliveredReports() {
        return undeliveredReports;
    }

    public void setUndeliveredReports(List<DeliveryReport> undeliveredReports) {
        this.undeliveredReports = undeliveredReports;
    }

    public List<GroupMessageDeliveryRequest> getGroupMessageDeliveries()
    {
        return groupMessageDeliveries;
    }

    public void setGroupMessageDeliveries(List<GroupMessageDeliveryRequest> groupMessageDeliveries)
    {
        this.groupMessageDeliveries = groupMessageDeliveries;
    }

    public SyncRequest getSyncRequest()
    {
        return syncRequest;
    }

    public void setSyncRequest(SyncRequest syncRequest)
    {
        this.syncRequest = syncRequest;
    }

    public HashMap<String, Object> getContact()
    {
        return contact;
    }

    public void setContact(HashMap<String, Object> contact)
    {
        this.contact = contact;
    }

    public HashMap<String, Object> getChatGroup()
    {
        return chatGroup;
    }

    public void setChatGroup(HashMap<String, Object> chatGroup)
    {
        this.chatGroup = chatGroup;
    }

    public List<HashMap<String, Object>> getChatGroupMembers()
    {
        return chatGroupMembers;
    }

    public void setChatGroupMembers(List<HashMap<String, Object>> chatGroupMembers)
    {
        this.chatGroupMembers = chatGroupMembers;
    }
}
