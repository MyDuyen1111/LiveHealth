package com.livehealth.auth;

import com.livehealth.auth.dto.request.otp.OtpType;

public interface OtpService {

    String generateOtp();

    void storeOtp(String email, String otp, OtpType type, String dataJson);

    boolean validateOtp(String email, String otp, OtpType type);

    String getOtpData(String email, OtpType type);

    void clearOtp(String email, OtpType type);

    boolean isOtpValid(String email, OtpType type);
}
