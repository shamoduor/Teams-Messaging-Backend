package com.shamine.teamsmessagingbackend.controllers;

import com.shamine.teamsmessagingbackend.dto.OTPDto;
import com.shamine.teamsmessagingbackend.services.OTPService;
import com.shamine.teamsmessagingbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OTPController {

    @Autowired
    private OTPService otpService;

    @PostMapping("/otp")
    public ResponseEntity generateOTP(@RequestBody OTPDto otpDto) {
        return otpService.generateOTP(otpDto.getEmail());
    }
}
