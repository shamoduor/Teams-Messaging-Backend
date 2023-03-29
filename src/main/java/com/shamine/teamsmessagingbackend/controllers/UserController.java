package com.shamine.teamsmessagingbackend.controllers;

import com.shamine.teamsmessagingbackend.dto.ResponseDto;
import com.shamine.teamsmessagingbackend.dto.UserDto;
import com.shamine.teamsmessagingbackend.entities.User;
import com.shamine.teamsmessagingbackend.models.responses.HmResponse;
import com.shamine.teamsmessagingbackend.security.MyUserDetails;
import com.shamine.teamsmessagingbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity register(@RequestBody UserDto user) {
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseEntity login(@RequestBody UserDto user) {
        return userService.login(user);
    }

    @PostMapping("/user/requestPasswordResetCode")
    public ResponseEntity requestPasswordResetCode(@RequestBody UserDto user) {
        return userService.requestPasswordResetCode(user);
    }

    @PutMapping("/user/resetPassword")
    public ResponseEntity changePassword(@RequestBody UserDto user) {
        return userService.changePassword(user);
    }

    @PutMapping("user/profilePic")
    public ResponseEntity<HashMap<String, Object>> changeProfilePicture(Authentication authentication, @RequestParam("pic") MultipartFile pic) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return userService.changeProfilePicture(loggedInUser, pic);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }

    @PutMapping("user/profileDetails")
    public ResponseEntity<HashMap<String, Object>> updateProfileDetails(Authentication authentication, @RequestBody User request) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return userService.updateProfileDetails(loggedInUser, request);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }

    @GetMapping("user/contacts")
    public ResponseEntity<HashMap<String, Object>> searchContacts(Authentication authentication, @RequestParam String searchText) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return userService.searchContacts(loggedInUser, searchText);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }

    @GetMapping("user/senderContact")
    public ResponseEntity<HashMap<String, Object>> searchSender(Authentication authentication, @RequestParam String searchId) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return userService.searchSender(loggedInUser, searchId);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }

    @PatchMapping("user")
    public ResponseEntity updateFCMToken(Authentication authentication, @RequestParam String fcmToken) {
        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails) {
            User loggedInUser = ((MyUserDetails) authentication.getPrincipal()).getLoggedInUser();
            return userService.updateFCMToken(loggedInUser, fcmToken);
        } else {
            HmResponse hmResponse = new HmResponse();
            hmResponse.setResponse(new ResponseDto("Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(hmResponse.getHashMap());
        }
    }
}
