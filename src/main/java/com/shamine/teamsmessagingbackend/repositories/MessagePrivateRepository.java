package com.shamine.teamsmessagingbackend.repositories;

import com.shamine.teamsmessagingbackend.entities.MessagePrivate;
import com.shamine.teamsmessagingbackend.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface MessagePrivateRepository extends CrudRepository<MessagePrivate, Integer>
{
    List<MessagePrivate> findAllBySenderOrRecipientOrderByCreatedOnAsc(User sender, User recipient);

    List<MessagePrivate> findAllBySenderOrRecipientAndReceivedOnIsNullOrderByCreatedOnAsc(User sender, User recipient);

    List<MessagePrivate> findAllByRecipientAndCreatedOnGreaterThanOrderByCreatedOnAsc(User recipient, Date lastSync);

    MessagePrivate findByMessageIdAndSender(int messageId, User sender);

    MessagePrivate findByMessageId(int messageId);
}
