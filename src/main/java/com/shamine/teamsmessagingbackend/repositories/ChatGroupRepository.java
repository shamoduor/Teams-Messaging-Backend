package com.shamine.teamsmessagingbackend.repositories;


import com.shamine.teamsmessagingbackend.entities.ChatGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatGroupRepository extends CrudRepository<ChatGroup, Integer>
{
    ChatGroup findByChatGroupIdAndAvailable(int chatGroupId, boolean available);

    @Query("SELECT g FROM ChatGroup g " +
            "INNER JOIN ChatGroupMember gm ON gm.chatGroup.chatGroupId = g.chatGroupId " +
            "INNER JOIN User u ON u.userId = gm.user.userId " +
            "WHERE u.userId = :userId AND g.available = true AND gm.available = true")
    List<ChatGroup> findAllChatGroupsByUserId(int userId);
}
