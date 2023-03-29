package com.shamine.teamsmessagingbackend.repositories;


import com.shamine.teamsmessagingbackend.entities.MessageGroup;
import com.shamine.teamsmessagingbackend.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface MessageGroupRepository extends CrudRepository<MessageGroup, Integer>
{
    @Query("SELECT g FROM MessageGroup g " +
            "INNER JOIN ChatGroupMember gm ON g.chatGroup.chatGroupId = gm.chatGroup.chatGroupId " +
            "WHERE gm.user.userId = :userId AND g.createdOn > :lastSync ORDER BY g.createdOn ASC")
    List<MessageGroup> findAllByToSync(int userId, Date lastSync);

    @Query("SELECT g FROM MessageGroup g " +
            "INNER JOIN ChatGroupMember gm ON g.chatGroup.chatGroupId = gm.chatGroup.chatGroupId " +
            "WHERE gm.user.userId = :userId ORDER BY g.createdOn ASC")
    List<MessageGroup> findAllByUserId(int userId);

    MessageGroup findByMessageIdAndSender(int messageId, User sender);

    MessageGroup findByMessageId(int messageId);
}
