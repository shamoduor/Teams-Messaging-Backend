package com.shamine.teamsmessagingbackend.services;

import com.shamine.teamsmessagingbackend.entities.GroupMessageDelivery;
import com.shamine.teamsmessagingbackend.models.requests.GroupMessageDeliveryRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupMessageDeliveryService {
    public GroupMessageDeliveryRequest groupMessageDeliveryToRequest(GroupMessageDelivery delivery) {
        GroupMessageDeliveryRequest request = new GroupMessageDeliveryRequest();
        request.setDeliveryId(delivery.getDeliveryId());
        request.setMessageGroupId(delivery.getMessage().getMessageId());
        request.setChatGroupMemberId(delivery.getRecipient().getMemberId());
        request.setUserId(delivery.getRecipient().getUser().getUserId());
        request.setReceivedOn(delivery.getReceivedOn() != null ? delivery.getReceivedOn().getTime() : 0);
        request.setReadOn(delivery.getReadOn() != null ? delivery.getReadOn().getTime() : null);

        return request;
    }

    public List<GroupMessageDeliveryRequest> groupMessageDeliveryListToRequest(List<GroupMessageDelivery> deliveries) {
        List<GroupMessageDeliveryRequest> list = new ArrayList<>();
        for (GroupMessageDelivery delivery : deliveries) {
            list.add(groupMessageDeliveryToRequest(delivery));
        }

        return list;
    }
}
