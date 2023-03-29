package com.shamine.teamsmessagingbackend.services;

import com.google.gson.Gson;
import com.shamine.teamsmessagingbackend.entities.User;
import com.shamine.teamsmessagingbackend.models.FCMData;
import com.shamine.teamsmessagingbackend.models.FCMMessage;
import com.shamine.teamsmessagingbackend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    @Async
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("teamsmessaging@gmail.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendFCMMessage(User user, String title, String message, String time, String sender, String receiver) {
        try {
            if (user != null && !Utils.stringNullOrEmpty(user.getFcmToken())) {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", "key=AAAAjd0jO6I:APA91bGis6oYtpHjSC19MNXAU71moBL9kV50U9lIJr1AsuU-FTWfNABmLeLnUO_grGoFUbcEOuj5OrQ9yU2WVG5xhclCSgZUNJ5Je9tjJAL3EPueyepEGwZkGD-Pi9q-mubmlOoFlEOD");
                con.setDoOutput(true);

                FCMData data = new FCMData(user.getPicUrl(), title, message, time, sender, receiver);
                FCMMessage fcmMessage = new FCMMessage(user.getFcmToken(), data);

                String requestBody = new Gson().toJson(fcmMessage);

                OutputStream os = con.getOutputStream();
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);

                int statusCode = con.getResponseCode();
                if (statusCode >= 200 && statusCode < 300) {
                    System.out.println("FCM sent successfully");
                } else {
                    System.out.println("FCM sending failed");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
