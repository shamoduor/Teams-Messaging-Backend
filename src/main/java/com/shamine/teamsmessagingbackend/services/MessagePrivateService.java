package com.shamine.teamsmessagingbackend.services;

import com.shamine.teamsmessagingbackend.entities.MessagePrivate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class MessagePrivateService {
    public HashMap<String, Object> messagePrivateToHashMap(MessagePrivate message) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("messageId", message.getMessageId());
        hashMap.put("content", message.getContent());
        hashMap.put("createdOn", message.getCreatedOn().getTime());
        hashMap.put("recipientId", message.getRecipient().getUserId());
        hashMap.put("senderId", message.getSender().getUserId());
        hashMap.put("availableForRecipient", message.isAvailableForRecipient());
        hashMap.put("availableForSender", message.isAvailableForSender());
        hashMap.put("receivedOn", message.getReceivedOn() != null ? message.getReceivedOn().getTime() : null);

        return hashMap;
    }

    public List<HashMap<String, Object>> messagePrivateListToHashMap(List<MessagePrivate> messages) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (MessagePrivate messagePrivate : messages) {
            list.add(messagePrivateToHashMap(messagePrivate));
        }

        return list;
    }
}
