package com.shamine.teamsmessagingbackend.models.requests;

import java.util.List;

public class SyncRequest {
    private List<MessagePrivateRequest> privateMessagesToSync;
    private List<GroupMessageDeliveryRequest> deliveriesToSync;
    private long lastSyncTimestamp;

    public List<MessagePrivateRequest> getPrivateMessagesToSync() {
        return privateMessagesToSync;
    }

    public void setPrivateMessagesToSync(List<MessagePrivateRequest> privateMessagesToSync) {
        this.privateMessagesToSync = privateMessagesToSync;
    }

    public List<GroupMessageDeliveryRequest> getDeliveriesToSync() {
        return deliveriesToSync;
    }

    public void setDeliveriesToSync(List<GroupMessageDeliveryRequest> deliveriesToSync) {
        this.deliveriesToSync = deliveriesToSync;
    }

    public long getLastSyncTimestamp() {
        return lastSyncTimestamp;
    }

    public void setLastSyncTimestamp(long lastSyncTimestamp) {
        this.lastSyncTimestamp = lastSyncTimestamp;
    }
}
