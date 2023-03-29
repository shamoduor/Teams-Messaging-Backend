package com.shamine.teamsmessagingbackend.services;

import com.shamine.teamsmessagingbackend.entities.ChatGroup;
import com.shamine.teamsmessagingbackend.entities.ChatGroupMember;
import com.shamine.teamsmessagingbackend.entities.User;
import com.shamine.teamsmessagingbackend.models.requests.AddGroupMembersRequest;
import com.shamine.teamsmessagingbackend.models.requests.CreateGroupRequest;
import com.shamine.teamsmessagingbackend.models.requests.RenameGroupRequest;
import com.shamine.teamsmessagingbackend.models.responses.HmResponse;
import com.shamine.teamsmessagingbackend.dto.ResponseDto;
import com.shamine.teamsmessagingbackend.repositories.ChatGroupMemberRepository;
import com.shamine.teamsmessagingbackend.repositories.ChatGroupRepository;
import com.shamine.teamsmessagingbackend.repositories.UserRepository;
import com.shamine.teamsmessagingbackend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ChatGroupService {
    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatGroupMemberRepository chatGroupMemberRepository;

    @Autowired
    private FilesStorageService filesStorageService;

    public ResponseEntity<HashMap<String, Object>> createChatGroup(User loggedInUser, CreateGroupRequest request) {
        HmResponse hmResponse = new HmResponse();
        try {
            if (request != null && request.getMemberIds() != null && !request.getMemberIds().isEmpty() && !Utils.stringNullOrEmpty(request.getGroupTitle())) {
                List<User> users = userRepository.findAllByUserIdIn(request.getMemberIds());

                if (users != null && !users.isEmpty()) {
                    ChatGroup chatGroupToCreate = new ChatGroup();
                    chatGroupToCreate.setTitle(request.getGroupTitle());
                    chatGroupToCreate.setCreatedBy(loggedInUser);
                    chatGroupToCreate.setCreatedOn(new Date());
                    chatGroupToCreate.setAvailable(true);

                    ChatGroup createdChatGroup = chatGroupRepository.save(chatGroupToCreate);
                    List<ChatGroupMember> groupMembersToAdd = createNewGroupMembersList(users, createdChatGroup, loggedInUser);
                    List<ChatGroupMember> addedGroupMembers = (List<ChatGroupMember>) chatGroupMemberRepository.saveAll(groupMembersToAdd);

                    if (!addedGroupMembers.isEmpty()) {
                        hmResponse.getHashMap().put("chatGroup", chatGroupToHashMap(createdChatGroup, loggedInUser));
                        hmResponse.getHashMap().put("chatGroupMembers", chatGroupMemberListToHashMap(addedGroupMembers));
                        hmResponse.setResponse(new ResponseDto("Success", "Chat group created successfully"));
                        return ResponseEntity.status(HttpStatus.CREATED).body(hmResponse.getHashMap());
                    } else {
                        chatGroupRepository.delete(createdChatGroup);
                        hmResponse.setResponse(new ResponseDto("Fail", "Failed, an error occurred while creating the chat group"));
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                    }
                } else {
                    hmResponse.setResponse(new ResponseDto("Invalid list", "Invalid list of users submitted"));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                }
            } else {
                hmResponse.setResponse(new ResponseDto("Invalid data", "Submit all the required details to create the chat group"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while creating the chat group";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }
    }

    public ResponseEntity<HashMap<String, Object>> addChatGroupMembers(User loggedInUser, AddGroupMembersRequest request) {
        HmResponse hmResponse = new HmResponse();
        try {
            if (request != null && request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
                ChatGroup group = chatGroupRepository.findByChatGroupIdAndAvailable(request.getGroupId(), true);
                if (group != null) {
                    List<ChatGroupMember> membersInGroup = chatGroupMemberRepository.findAllByChatGroupAndAvailable(group, true);
                    List<User> usersInRequest = userRepository.findAllByUserIdIn(request.getMemberIds());

                    if (membersInGroup != null && usersInRequest != null && !usersInRequest.isEmpty()) {
                        List<ChatGroupMember> membersToReactivate = new ArrayList<>();

                        for (Iterator<User> srIterator = usersInRequest.iterator(); srIterator.hasNext(); ) {
                            User member = srIterator.next();

                            for (ChatGroupMember s : membersInGroup) {
                                if (s.getUser().getUserId() == member.getUserId()) {
                                    if (!s.isAvailable()) {
                                        s.setAvailable(true);
                                        s.setAddedOn(new Date());
                                        membersToReactivate.add(s);
                                    }
                                    srIterator.remove();
                                }
                            }
                        }

                        List<ChatGroupMember> membersToAdd = createNewGroupMembersList(usersInRequest, group, loggedInUser);
                        membersToAdd.addAll(membersToReactivate);
                        chatGroupMemberRepository.saveAll(membersToAdd);

                        List<ChatGroupMember> newMembersList = chatGroupMemberRepository.findAllByChatGroup(group);
                        if (newMembersList != null) {
                            hmResponse.setResponse(new ResponseDto("Success", "Chat group members added successfully"));
                            hmResponse.getHashMap().put("chatGroup", chatGroupToHashMap(group, loggedInUser));
                            hmResponse.getHashMap().put("chatGroupMembers", chatGroupMemberListToHashMap(newMembersList));

                            return ResponseEntity.status(HttpStatus.CREATED).body(hmResponse.getHashMap());
                        } else {
                            hmResponse.setResponse(new ResponseDto("Fail", "Failed, an error occurred while adding chat group members"));
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                        }
                    } else {
                        hmResponse.setResponse(new ResponseDto("Nothing to add", "There is no new group member to be added from the submitted list"));
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                    }
                } else {
                    hmResponse.setResponse(new ResponseDto("Invalid group", "Invalid chat group"));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                }
            } else {
                hmResponse.setResponse(new ResponseDto("Invalid data", "Submit all the required details to add members to the chat group"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while adding chat group members";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }
    }

    private List<ChatGroupMember> createNewGroupMembersList(List<User> users, ChatGroup chatGroup, User loggedInUser) {
        List<ChatGroupMember> groupMembersToAdd = new ArrayList<>();
        for (User u : users) {
            ChatGroupMember c = new ChatGroupMember();
            c.setChatGroup(chatGroup);
            c.setUser(u);
            c.setAddedOn(new Date());
            c.setAvailable(true);
            c.setAdmin(u.getUserId() == loggedInUser.getUserId());

            groupMembersToAdd.add(c);
        }
        return groupMembersToAdd;
    }

    public ResponseEntity<HashMap<String, Object>> removeChatGroupMember(User loggedInUser, int chatGroupMemberId) {
        HmResponse hmResponse = new HmResponse();
        try {
            ChatGroupMember memberToRemove = chatGroupMemberRepository.findByMemberIdAndAvailable(chatGroupMemberId, true);
            if (memberToRemove != null) {
                ChatGroupMember remover = chatGroupMemberRepository.findByChatGroupAndUserAndIsAdmin(memberToRemove.getChatGroup(), loggedInUser, true);
                if (remover != null) {
                    memberToRemove.setAvailable(false);
                    ChatGroupMember removedMember = chatGroupMemberRepository.save(memberToRemove);
                    if (removedMember.getMemberId() == memberToRemove.getMemberId() && !removedMember.isAvailable()) {
                        hmResponse.setResponse(new ResponseDto("Success", "Member removed successfully"));
                        hmResponse.getHashMap().put("removedMember", chatGroupMemberToHashMap(removedMember));
                        return ResponseEntity.status(HttpStatus.OK).body(hmResponse.getHashMap());
                    } else {
                        hmResponse.setResponse(new ResponseDto("Fail", "An error occurred while removing the group member. Please retry later"));
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                    }
                } else {
                    hmResponse.setResponse(new ResponseDto("Unauthorized", "You do not have the required permissions to remove a member from this group"));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                }
            } else {
                hmResponse.setResponse(new ResponseDto("Invalid member", "Invalid chat group member"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while removing the chat group member";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }

    }

    public ResponseEntity<HashMap<String, Object>> deleteChatGroup(User loggedInUser, int chatGroupId) {
        HmResponse hmResponse = new HmResponse();
        try {
            ChatGroup groupToDelete = chatGroupRepository.findByChatGroupIdAndAvailable(chatGroupId, true);
            if (groupToDelete != null) {
                ChatGroupMember remover = chatGroupMemberRepository.findByChatGroupAndUserAndIsAdmin(groupToDelete, loggedInUser, true);
                if (remover != null) {
                    groupToDelete.setAvailable(false);
                    ChatGroup deletedGroup = chatGroupRepository.save(groupToDelete);
                    if (deletedGroup.getChatGroupId() == groupToDelete.getChatGroupId() && !deletedGroup.isAvailable()) {
                        hmResponse.setResponse(new ResponseDto("Success", "Group deleted successfully"));
                        hmResponse.getHashMap().put("deletedGroup", chatGroupToHashMap(deletedGroup, loggedInUser));
                        return ResponseEntity.status(HttpStatus.OK).body(hmResponse.getHashMap());
                    } else {
                        hmResponse.setResponse(new ResponseDto("Fail", "An error occurred while removing the group member. Please retry later"));
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                    }
                } else {
                    hmResponse.setResponse(new ResponseDto("Unauthorized", "You do not have the required permissions to delete this group"));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                }
            } else {
                hmResponse.setResponse(new ResponseDto("Invalid group", "Invalid chat group"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while deleting the chat group";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }

    }


    public ResponseEntity<HashMap<String, Object>> renameChatGroup(User loggedInUser, RenameGroupRequest request) {
        HmResponse hmResponse = new HmResponse();
        try {
            if (request != null && !Utils.stringNullOrEmpty(request.getTitle())) {
                ChatGroup groupToRename = chatGroupRepository.findByChatGroupIdAndAvailable(request.getGroupId(), true);
                if (groupToRename != null) {
                    ChatGroupMember editor = chatGroupMemberRepository.findByChatGroupAndUserAndIsAdmin(groupToRename, loggedInUser, true);
                    if (editor != null) {
                        if (!request.getTitle().equals(groupToRename.getTitle())) {
                            groupToRename.setTitle(request.getTitle());
                            ChatGroup renamedGroup = chatGroupRepository.save(groupToRename);
                            if (renamedGroup.getTitle().equals(request.getTitle())) {
                                List<ChatGroupMember> chatGroupMembers = chatGroupMemberRepository.findAllByChatGroup(renamedGroup);
                                hmResponse.setResponse(new ResponseDto("Success", "Group renamed successfully"));
                                hmResponse.getHashMap().put("chatGroup", chatGroupToHashMap(renamedGroup, loggedInUser));
                                if (chatGroupMembers != null) {
                                    hmResponse.getHashMap().put("chatGroupMembers", chatGroupMemberListToHashMap(chatGroupMembers));
                                }
                                return ResponseEntity.status(HttpStatus.OK).body(hmResponse.getHashMap());
                            } else {
                                hmResponse.setResponse(new ResponseDto("Fail", "An error occurred while removing the group member. Please retry later"));
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                            }
                        } else {
                            hmResponse.setResponse(new ResponseDto("Nothing to change", "You cannot use the old group title as its new title"));
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                        }
                    } else {
                        hmResponse.setResponse(new ResponseDto("Unauthorized", "You do not have the required permissions to delete this group"));
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                    }
                } else {
                    hmResponse.setResponse(new ResponseDto("Invalid group", "Invalid chat group"));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                }
            } else {
                hmResponse.setResponse(new ResponseDto("Invalid group title", "Invalid chat group title"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            }

        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while renaming the chat group";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }

    }

    public ResponseEntity<HashMap<String, Object>> changeGroupPicture(User loggedInUser, MultipartFile pic, int groupId) {
        HmResponse hmResponse = new HmResponse();
        try {
            ChatGroup chatGroup = chatGroupRepository.findByChatGroupIdAndAvailable(groupId, true);
            if (pic != null && chatGroup != null) {
                ChatGroupMember editor = chatGroupMemberRepository.findByChatGroupAndUserAndIsAdmin(chatGroup, loggedInUser, true);
                if (editor != null) {
                    final String baseDir = "/opt/lampp/htdocs";
                    final String serverDir = "/BackendFiles/TeamsMessagingApp/images/groups";
                    final String hardDiskPath = baseDir + serverDir;
                    String fileName = loggedInUser.getUserId() + "" + new Date().getTime() + ".jpg";

                    if (filesStorageService.save(pic, hardDiskPath, fileName)) {
                        String downloadUrl = serverDir + "/" + fileName;
                        chatGroup.setPicUrl(downloadUrl);

                        ChatGroup savedChatGroup = chatGroupRepository.save(chatGroup);
                        if (chatGroup.getPicUrl().equals(downloadUrl)) {
                            List<ChatGroupMember> chatGroupMembers = chatGroupMemberRepository.findAllByChatGroup(savedChatGroup);
                            hmResponse.setResponse(new ResponseDto("Success", "Group picture changed successfully"));
                            hmResponse.getHashMap().put("chatGroup", chatGroupToHashMap(savedChatGroup, loggedInUser));
                            if (chatGroupMembers != null) {
                                hmResponse.getHashMap().put("chatGroupMembers", chatGroupMemberListToHashMap(chatGroupMembers));
                            }
                            return ResponseEntity.status(HttpStatus.OK).body(hmResponse.getHashMap());
                        } else {
                            hmResponse.setResponse(new ResponseDto("Server Error", "Unable to save profile picture. Try again later"));
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                        }
                    } else {
                        hmResponse.setResponse(new ResponseDto("Server Error", "Unable to save group picture. Retry later"));
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                    }
                } else {
                    hmResponse.setResponse(new ResponseDto("Unauthorized", "You do not have the required permissions to change the group picture"));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                }
            } else {
                if (pic == null) {
                    hmResponse.setResponse(new ResponseDto("Invalid file", "Submit a valid file to be saved"));
                } else {
                    hmResponse.setResponse(new ResponseDto("Invalid chat group", "Chat group does not exist"));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while changing the group picture";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }
    }

    public static HashMap<String, Object> chatGroupToHashMap(ChatGroup chatGroup, User loggedInUser) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("chatGroupId", chatGroup.getChatGroupId());
        hashMap.put("title", chatGroup.getTitle());
        hashMap.put("createdBy", chatGroup.getCreatedBy().getUserId());
        hashMap.put("createdOn", chatGroup.getCreatedOn().getTime());
        hashMap.put("available", chatGroup.isAvailable());
        hashMap.put("loggedInUserId", loggedInUser.getUserId());
        hashMap.put("picUrl", chatGroup.getPicUrl());

        return hashMap;
    }

    public List<HashMap<String, Object>> chatGroupListToHashMap(List<ChatGroup> chatGroups, User loggedInUser) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (ChatGroup chatGroup : chatGroups) {
            list.add(chatGroupToHashMap(chatGroup, loggedInUser));
        }

        return list;
    }

    private static HashMap<String, Object> chatGroupMemberToHashMap(ChatGroupMember groupMember) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("memberId", groupMember.getMemberId());
        hashMap.put("chatGroupId", groupMember.getChatGroup().getChatGroupId());
        hashMap.put("userId", groupMember.getUser().getUserId());
        hashMap.put("addedOn", groupMember.getAddedOn().getTime());
        hashMap.put("available", groupMember.isAvailable());
        hashMap.put("isAdmin", groupMember.isAdmin());
        hashMap.put("fullName", groupMember.getUser().getName());
        hashMap.put("username", groupMember.getUser().getUsername());
        hashMap.put("picUrl", groupMember.getUser().getPicUrl());

        return hashMap;
    }

    public static List<HashMap<String, Object>> chatGroupMemberListToHashMap(List<ChatGroupMember> groupMembers) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (ChatGroupMember groupMember : groupMembers) {
            list.add(chatGroupMemberToHashMap(groupMember));
        }

        return list;
    }
}
