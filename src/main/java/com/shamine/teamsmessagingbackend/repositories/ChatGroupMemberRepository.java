package com.shamine.teamsmessagingbackend.repositories;


import com.shamine.teamsmessagingbackend.entities.ChatGroup;
import com.shamine.teamsmessagingbackend.entities.ChatGroupMember;
import com.shamine.teamsmessagingbackend.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatGroupMemberRepository extends CrudRepository<ChatGroupMember, Integer>
{
    List<ChatGroupMember> findAllByChatGroupAndAvailable(ChatGroup chatGroup, boolean available);

    List<ChatGroupMember> findAllByChatGroup(ChatGroup chatGroup);

    ChatGroupMember findByUserAndChatGroupAndAvailable(User user, ChatGroup chatGroup, boolean available);

    ChatGroupMember findByMemberIdAndAvailable(int memberId, boolean available);

    ChatGroupMember findByChatGroupAndUserAndIsAdmin(ChatGroup chatGroup, User user, boolean isAdmin);
}
