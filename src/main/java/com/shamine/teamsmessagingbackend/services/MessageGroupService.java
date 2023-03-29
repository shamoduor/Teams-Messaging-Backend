package com.shamine.teamsmessagingbackend.services;

import com.shamine.teamsmessagingbackend.entities.MessageGroup;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class MessageGroupService {

    public HashMap<String, Object> messageGroupToHashMap(MessageGroup message) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("messageId", message.getMessageId());
        hashMap.put("content", message.getContent());
        hashMap.put("createdOn", message.getCreatedOn().getTime());
        hashMap.put("recipientId", message.getChatGroup().getChatGroupId());
        hashMap.put("senderId", message.getSender().getUserId());
        hashMap.put("senderName", message.getSender().getName());

        return hashMap;
    }

    public List<HashMap<String, Object>> messageGroupListToHashMap(List<MessageGroup> messages) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (MessageGroup messageGroup : messages) {
            list.add(messageGroupToHashMap(messageGroup));
        }

        return list;
    }
}
