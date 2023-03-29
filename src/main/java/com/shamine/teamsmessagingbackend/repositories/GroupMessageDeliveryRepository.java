package com.shamine.teamsmessagingbackend.repositories;

import com.shamine.teamsmessagingbackend.entities.ChatGroupMember;
import com.shamine.teamsmessagingbackend.entities.GroupMessageDelivery;
import com.shamine.teamsmessagingbackend.entities.MessageGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface GroupMessageDeliveryRepository extends CrudRepository<GroupMessageDelivery, Integer> {

    //all pending deliveries to the member
    List<GroupMessageDelivery> findAllByRecipientAndReceivedOnIsNullOrderByDeliveryIdAsc(ChatGroupMember recipient);

    GroupMessageDelivery findByMessageAndRecipient(MessageGroup message, ChatGroupMember recipient);

    //all deliveries for a particular message
    List<GroupMessageDelivery> findAllByMessage(MessageGroup message);

    GroupMessageDelivery findByDeliveryId(int deliveryId);

    @Query("SELECT d FROM GroupMessageDelivery d WHERE d.recipient.user.userId = :userId " +
            "OR d.message.sender.userId = :userId")
    List<GroupMessageDelivery> findAllByUserId(int userId);

    @Query("SELECT d FROM GroupMessageDelivery d WHERE " +
            "(d.recipient.user.userId = :userId OR d.message.sender.userId = :userId) " +
            "AND (d.message.createdOn > :lastSyncDate OR d.receivedOn > :lastSyncDate OR d.readOn > :lastSyncDate)")
    List<GroupMessageDelivery> findAllByUserIdToSync(int userId, Date lastSyncDate);
}
