package com.shamine.teamsmessagingbackend.services;

import com.shamine.teamsmessagingbackend.dto.ResponseDto;
import com.shamine.teamsmessagingbackend.dto.UserDto;
import com.shamine.teamsmessagingbackend.entities.OTP;
import com.shamine.teamsmessagingbackend.entities.User;
import com.shamine.teamsmessagingbackend.enums.OTPStatus;
import com.shamine.teamsmessagingbackend.models.responses.HmResponse;
import com.shamine.teamsmessagingbackend.repositories.OTPRepository;
import com.shamine.teamsmessagingbackend.repositories.UserRepository;
import com.shamine.teamsmessagingbackend.security.JwtService;
import com.shamine.teamsmessagingbackend.security.MyUserDetails;
import com.shamine.teamsmessagingbackend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPService otpService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private FilesStorageService filesStorageService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            return new MyUserDetails(user);
        } else {
            throw new UsernameNotFoundException("User with email: " + email + " not found");
        }
    }

    public ResponseEntity register(UserDto request) {
        try {
            if (request != null) {
                String name = Utils.cleanString(Utils.authDecrypt(request.getName(), request.getAuthTime()));
                String username = Utils.cleanString(Utils.authDecrypt(request.getUsername(), request.getAuthTime()));
                String email = Utils.cleanString(Utils.authDecrypt(request.getEmail(), request.getAuthTime()));
                String password = Utils.authDecrypt(request.getPassword(), request.getAuthTime());

                if (Utils.stringNullOrEmpty(name) || Utils.stringNullOrEmpty(username) ||
                        Utils.stringNullOrEmpty(email) || Utils.stringNullOrEmpty(password)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Missing details", "Some required fields are missing"));
                } else if (!Utils.isValidEmailAddress(email)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid email address", "Please enter a valid email address"));
                } else if (!Utils.isValidName(name)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid name", "Please enter a valid name"));
                } else if (!Utils.isValidUsername(username)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid username", "Please enter a valid username"));
                } else if (!Utils.isValidOTP(request.getOtpCode())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid OTP", "Please enter a valid OTP"));
                } else if (userRepository.existsByEmail(email)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("User Exists", "User with email address: " + email + " already exists. " + "Ensure that you have not mistyped your email address, " + "else proceed to login"));
                } else if (userRepository.existsByUsername(username)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Username already taken", "Username: " + username + " has already been taken, please choose a different username"));
                } else {
                    OTPStatus otpStatus = otpService.getOTPValidity(email, request.getOtpCode());
                    if (otpStatus == OTPStatus.EXPIRED) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Expired OTP", "Please enter a valid OTP. The submitted one has expired."));
                    } else if (otpStatus == OTPStatus.USED) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Used OTP", "Please enter a valid OTP. The submitted one had already been used."));
                    } else if (otpStatus == OTPStatus.VALID) {
                        User userToSave = new User();
                        userToSave.setName(name);
                        userToSave.setUsername(username);
                        userToSave.setEmail(email);
                        userToSave.setPassword(Utils.getPasswordEncoder().encode(password));
                        userToSave.setCreatedOn(new Date());

                        User u = userRepository.save(userToSave);

                        if (u.getUserId() > 0) {

                            final UserDetails userDetails = loadUserByUsername(email);
                            final String jwt = jwtService.generateToken(userDetails);

                            HashMap<String, Object> hm = new HashMap<>();
                            hm.put("user", userToDto(u, jwt));
                            hm.put("response", new ResponseDto("Success", "Welcome"));
                            return ResponseEntity.status(HttpStatus.CREATED).body(hm);
                        } else {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("Error", "An error occurred while saving user details, please try again later"));
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid OTP", "Please enter a valid OTP"));
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Null user", "Submit a user with all the required fields to be saved"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while registering user";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("Error", message));
        }
    }

    public ResponseEntity login(UserDto request) {
        try {
            if (request == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Null user", "Submit all the required user details"));
            }

            String email = Utils.cleanString(request.getEmail());
            if (Utils.stringNullOrEmpty(email) || Utils.stringNullOrEmpty(request.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Missing details", "Some required fields are missing"));
            } else if (!Utils.isValidEmailAddress(email)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid email address", "Please enter a valid email address"));
            } else {
                //error will be thrown if authenticationManager.authenticate is not successful
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
                final UserDetails userDetails = loadUserByUsername(request.getEmail());

                final User gottenUser = userRepository.findByEmail(request.getEmail());
                final String jwt = jwtService.generateToken(userDetails);

                HashMap<String, Object> hm = new HashMap<>();
                hm.put("user", userToDto(gottenUser, jwt));
                hm.put("response", new ResponseDto("Success", "Welcome back"));
                return ResponseEntity.status(HttpStatus.OK).body(hm);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BadCredentialsException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Error", "Invalid credentials"));
            } else {
                String message = e.getMessage() != null ? e.getMessage() : "An error occurred while logging in";
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("Error", message));
            }
        }
    }

    public ResponseEntity requestPasswordResetCode(UserDto request) {
        try {
            if (request != null && request.getEmail() != null) {
                String email = Utils.cleanString(request.getEmail());

                if (!Utils.stringNullOrEmpty(email) && Utils.isValidEmailAddress(email)) {
                    User gottenUser = userRepository.findByEmail(email);

                    if (gottenUser != null) {
                        int validHours = 1;
                        long oneHrInMillis = 1000 * 60 * 60;
                        long passwordResetCodeExpiry = new Date().getTime() + (validHours * oneHrInMillis);
                        int generatedOTP = Utils.generateRandomNumber(Utils.OTP_LOWER_LIMIT, Utils.OTP_UPPER_LIMIT);

                        OTP otp = new OTP();
                        otp.setEmail(email);
                        otp.setOtpCode(generatedOTP);
                        otp.setCreatedAt(new Date());
                        otp.setExpiryTime(new Date(passwordResetCodeExpiry));

                        otpRepository.save(otp);

                        String emailBody = "Hi " + gottenUser.getName() + ",\n\n" +
                                "Your password reset code is: " + otp.getOtpCode() + "\n\n" +
                                "This reset code is valid for only " + validHours + " hour. " +
                                "Ignore this email if you did not request a password reset.\n\n" +
                                "Regards,\n" +
                                "Teams Messaging.";

                        mailService.sendEmail(email, "Password Reset - Teams Messaging", emailBody);
                        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Success", "Check your email for the password reset code"));
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Error", "User with email address " + email + " does not exist. Try registering instead"));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid email address", "Please enter a valid email address"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Null email", "Submit the user's email address to initiate the password reset process"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while requesting the password reset code";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("Error", message));
        }
    }

    public ResponseEntity changePassword(UserDto request) {
        try {
            if (request != null &&
                    !Utils.stringNullOrEmpty(request.getEmail()) &&
                    !Utils.stringNullOrEmpty(request.getPassword()) &&
                    request.getOtpCode() != null) {
                String email = Utils.cleanString(request.getEmail());

                if (!Utils.stringNullOrEmpty(email) && Utils.isValidEmailAddress(email)) {
                    User gottenUser = userRepository.findByEmail(email);

                    if (gottenUser != null) {
                        OTPStatus otpStatus = otpService.getOTPValidity(email, request.getOtpCode());

                        if (otpStatus == OTPStatus.EXPIRED) {
                            if (requestPasswordResetCode(request).getStatusCode() == HttpStatus.OK) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Expired", "Your password reset code had expired, check your email for the new password reset code"));
                            } else {
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("Error", "An error occurred while changing the password"));
                            }
                        } else if (otpStatus == OTPStatus.VALID) {
                            gottenUser.setPassword(Utils.getPasswordEncoder().encode(request.getPassword()));
                            userRepository.save(gottenUser);
                            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Success", "Password changed successfully, proceed to login"));
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid Reset Code", "The submitted password reset code is invalid"));
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Error", "User with email address " + email + " does not exist"));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid email address", "Please enter a valid email address"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Incomplete user", "Submit a user with all the required fields"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while changing the password";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("Error", message));
        }
    }

    private ResponseEntity resendPasswordResetCode(UserDto request, String badRequestMessage) {
        ResponseEntity resendResponse = requestPasswordResetCode(request);
        if (resendResponse.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Reset Code Reset", badRequestMessage));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("Error", "An error occurred, please try again later"));
        }
    }

    public ResponseEntity<HashMap<String, Object>> changeProfilePicture(User loggedInUser, MultipartFile pic) {
        HmResponse hmResponse = new HmResponse();
        try {
            if (pic != null) {
                ///opt/lampp/htdocs/

                final String baseDir = "/opt/lampp/htdocs";
                final String serverDir = "/BackendFiles/TeamsMessagingApp/images/profile";
                final String hardDiskPath = baseDir + serverDir;
                String fileName = loggedInUser.getUserId() + "" + new Date().getTime() + ".jpg";

                if (filesStorageService.save(pic, hardDiskPath, fileName)) {
                    String downloadUrl = serverDir + "/" + fileName;
                    loggedInUser.setPicUrl(downloadUrl);

                    User savedUser = userRepository.save(loggedInUser);
                    if (savedUser.getPicUrl().equals(downloadUrl)) {
                        hmResponse.getHashMap().put("user", userToDto(savedUser, null));
                        hmResponse.setResponse(new ResponseDto("Success", "Profile picture changed successfully"));

                        return ResponseEntity.status(HttpStatus.OK).body(hmResponse.getHashMap());
                    } else {
                        hmResponse.setResponse(new ResponseDto("Server Error", "Unable to save profile picture. Try again later"));
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                    }
                } else {
                    hmResponse.setResponse(new ResponseDto("Server Error", "Unable to save profile picture. Retry later"));
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
                }
            } else {
                hmResponse.setResponse(new ResponseDto("Invalid file", "Submit a valid file to be saved"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while changing the profile picture";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }
    }

    public ResponseEntity<HashMap<String, Object>> updateProfileDetails(User loggedInUser, User request) {
        HmResponse hmResponse = new HmResponse();
        try {
            if (request != null && (Utils.stringNullOrEmpty(request.getUsername()) || Utils.stringNullOrEmpty(request.getName()))) {
                String newUsername = Utils.cleanString(request.getUsername());
                String newName = Utils.cleanString(request.getName());
                if ((newUsername != null && !newUsername.equals(loggedInUser.getUsername())) ||
                        (newName != null && !newName.equals(loggedInUser.getName()))) {
                    boolean toChangeUsername;
                    if (newName != null) {
                        loggedInUser.setName(newName);
                        toChangeUsername = false;
                    } else {
                        if (userRepository.existsByUsername(newUsername)) {
                            hmResponse.setResponse(new ResponseDto("Username already taken", "Username: " + newUsername + " has already been taken, please choose a different username"));
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                        } else {
                            loggedInUser.setUsername(newUsername);
                            toChangeUsername = true;
                        }
                    }

                    User savedUser = userRepository.save(loggedInUser);
                    hmResponse.getHashMap().put("user", userToDto(savedUser, null));
                    hmResponse.setResponse(new ResponseDto("Success", (toChangeUsername ? "Username" : "Display name") + " updated successfully"));

                    return ResponseEntity.status(HttpStatus.OK).body(hmResponse.getHashMap());
                } else {
                    hmResponse.setResponse(new ResponseDto("No change", "No changes to be made, the new value is the same as the old value"));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
                }
            } else {
                hmResponse.setResponse(new ResponseDto("Incomplete request", "Some required fields are missing"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while updating your profile details";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }
    }

    public ResponseEntity<HashMap<String, Object>> searchContacts(User loggedInUser, String searchText) {
        HmResponse hmResponse = new HmResponse();
        try {
            if (Utils.stringNullOrEmpty(searchText)) {
                hmResponse.setResponse(new ResponseDto("Invalid text", "Enter a valid text, search by display name or username"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            } else {
                List<User> result = userRepository.findFirst20ByUsernameContainingOrNameContainingOrderByUsernameAsc(searchText, searchText);
                for (Iterator<User> iterator = result.iterator(); iterator.hasNext(); ) {
                    User u = iterator.next();
                    if (u.getUserId() == loggedInUser.getUserId()) {
                        iterator.remove();
                        break;
                    }
                }
                hmResponse.setResponse(new ResponseDto("Success", "Search successful"));
                hmResponse.getHashMap().put("searchResult", usersToContactHashMap(result, loggedInUser));
                return ResponseEntity.status(HttpStatus.OK).body(hmResponse.getHashMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while searching for the user";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }
    }

    public ResponseEntity<HashMap<String, Object>> searchSender(User loggedInUser, String searchId) {
        HmResponse hmResponse = new HmResponse();
        try {
            if (Utils.stringNullOrEmpty(searchId)) {
                hmResponse.setResponse(new ResponseDto("Invalid text", "Enter a valid text, search by display name or username"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hmResponse.getHashMap());
            } else {
                User result = userRepository.findByUserId(Integer.parseInt(searchId));
                hmResponse.setResponse(new ResponseDto("Success", "Search successful"));
                hmResponse.getHashMap().put("searchResult", userToDto(result, null));
                return ResponseEntity.status(HttpStatus.OK).body(hmResponse.getHashMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while searching for the user";
            hmResponse.setResponse(new ResponseDto("Error", message));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hmResponse.getHashMap());
        }
    }

    public ResponseEntity updateFCMToken(User loggedInUser, String fcmToken) {
        try {
            if (Utils.stringNullOrEmpty(fcmToken)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Invalid FCM token", "Kindly provide a valid FCM token"));
            }

            if (loggedInUser.getFcmToken() == null || !loggedInUser.getFcmToken().equals(fcmToken)) {
                loggedInUser.setFcmToken(fcmToken);
                userRepository.save(loggedInUser);
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Success", "FCM token updated successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage() != null ? e.getMessage() : "An error occurred while updating the FCM token";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("Error", message));
        }
    }

    private UserDto userToDto(User user, String jwt) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPicUrl(user.getPicUrl());
        dto.setCreatedOn(user.getCreatedOn().getTime());

        if (jwt != null) {
            dto.setToken(jwt);
        }

        return dto;
    }

    private List<HashMap<String, Object>> usersToContactHashMap(List<User> users, User loggedInUser) {
        List<HashMap<String, Object>> contactList = new ArrayList<>();
        for (User user : users) {
            HashMap<String, Object> hashMap = userToContactHashMap(user);
            hashMap.put("loggedInUserId", loggedInUser.getUserId());
            contactList.add(hashMap);
        }
        return contactList;
    }

    public static HashMap<String, Object> userToContactHashMap(User user) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", user.getUserId());
        hashMap.put("username", user.getUsername());
        hashMap.put("name", user.getName());
        hashMap.put("picUrl", user.getPicUrl());

        return hashMap;
    }
}
