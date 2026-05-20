package com.livehealth.auth;

public interface EmailService {

    void sendOtpEmail(String email, String otp);

}
