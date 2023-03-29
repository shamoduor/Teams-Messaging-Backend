package com.shamine.teamsmessagingbackend.services;

import com.shamine.teamsmessagingbackend.dto.ResponseDto;
import com.shamine.teamsmessagingbackend.entities.OTP;
import com.shamine.teamsmessagingbackend.enums.OTPStatus;
import com.shamine.teamsmessagingbackend.repositories.OTPRepository;
import com.shamine.teamsmessagingbackend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OTPService {

    @Autowired
    private MailService mailService;

    @Autowired
    private OTPRepository otpRepository;

    private final long otpLifespanInMillis = 1000 * 60 * 5;

    public ResponseEntity generateOTP(String emailAddress) {
        try {
            if (Utils.isValidEmailAddress(emailAddress)) {
                int generatedOTP = Utils.generateRandomNumber(Utils.OTP_LOWER_LIMIT, Utils.OTP_UPPER_LIMIT);

                Date creationTime = new Date();
                Date expiryTime = new Date(creationTime.getTime() + otpLifespanInMillis);

                OTP otp = new OTP();
                otp.setEmail(emailAddress);
                otp.setOtpCode(generatedOTP);
                otp.setCreatedAt(creationTime);
                otp.setExpiryTime(expiryTime);

                otpRepository.save(otp);

                String subject = "Teams Messaging OTP";
                String body = "Hello,\n\n" + "Your OTP is: " + otp.getOtpCode() + "\n\nRegards,\nTeams Messaging.";
                mailService.sendEmail(emailAddress, subject, body);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Success", "OTP sent"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid email address", "Please enter a valid email address"));
            }
        } catch (Exception e) {
            e.printStackTrace();

            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while generating the OTP";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("Error", message));
        }
    }

    public OTPStatus getOTPValidity(String email, int otpCode) {
        List<OTP> otpList = otpRepository.findByEmailAndOtpCode(email, otpCode, PageRequest.of(0, 1));
        OTP otp = null;
        if (otpList != null && !otpList.isEmpty()) {
            otp = otpList.get(0);
        }

        if (otp == null) {
            return OTPStatus.INVALID;
        }

        if (otp.getHasBeenUsed() != null && otp.getHasBeenUsed()) {
            return OTPStatus.USED;
        } else if (otp.getExpiryTime().getTime() > new Date().getTime()) {
            return OTPStatus.VALID;
        } else {
            return OTPStatus.EXPIRED;
        }
    }
}
