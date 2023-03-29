package com.shamine.teamsmessagingbackend.controllers;

import com.shamine.teamsmessagingbackend.dto.ResponseDto;
import com.shamine.teamsmessagingbackend.entities.User;
import com.shamine.teamsmessagingbackend.models.requests.AddGroupMembersRequest;
import com.shamine.teamsmessagingbackend.models.requests.CreateGroupRequest;
import com.shamine.teamsmessagingbackend.models.requests.RenameGroupRequest;
import com.shamine.teamsmessagingbackend.models.responses.HmResponse;
import com.shamine.teamsmessagingbackend.security.MyUserDetails;
import com.shamine.teamsmessagingbackend.services.ChatGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@RestController
public class ChatGroupController {
    @Autowired
    private ChatGroupService chatGroupService;

    @PostMapping("/group")
    public ResponseEntity<HashMap<String, Object>> createChatGroup(Authentication authentication, @RequestBody CreateGroupRequest request) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return chatGroupService.createChatGroup(loggedInUser, request);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }

    @DeleteMapping("group/{chatGroupId}")
    public ResponseEntity<HashMap<String, Object>> deleteChatGroup(Authentication authentication, @PathVariable int chatGroupId) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return chatGroupService.deleteChatGroup(loggedInUser, chatGroupId);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }

    @PostMapping("/group/member")
    public ResponseEntity<HashMap<String, Object>> addChatGroupMembers(Authentication authentication, @RequestBody AddGroupMembersRequest request) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return chatGroupService.addChatGroupMembers(loggedInUser, request);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }

    @DeleteMapping("group/member/{chatGroupMemberId}")
    public ResponseEntity<HashMap<String, Object>> removeChatGroupMember(Authentication authentication, @PathVariable int chatGroupMemberId) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return chatGroupService.removeChatGroupMember(loggedInUser, chatGroupMemberId);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }

    @PutMapping("/group")
    public ResponseEntity<HashMap<String, Object>> renameChatGroup(Authentication authentication, @RequestBody RenameGroupRequest request) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return chatGroupService.renameChatGroup(loggedInUser, request);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }

    @PutMapping("group/{groupId}/pic")
    public ResponseEntity<HashMap<String, Object>> changeGroupPicture(Authentication authentication, @RequestParam("pic") MultipartFile pic, @PathVariable int groupId) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return chatGroupService.changeGroupPicture(loggedInUser, pic, groupId);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }
}
