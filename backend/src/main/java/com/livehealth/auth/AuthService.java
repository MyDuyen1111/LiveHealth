package com.livehealth.auth;

import com.livehealth.auth.dto.request.*;
import com.livehealth.auth.dto.request.otp.VerifyOtpRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.auth.dto.response.LoginResponseDto;
import com.livehealth.auth.dto.response.TokenRefreshResponseDto;
import com.livehealth.user.dto.response.user.UserResponseDto;

public interface AuthService {

    LoginResponseDto authentication(LoginRequestDto request);

    CommonResponseDto logout(LogoutRequestDto request);

    TokenRefreshResponseDto refresh(TokenRefreshRequestDto request);

    CommonResponseDto register(RegisterRequestDto request);

    CommonResponseDto forgotPassword(ForgotPasswordRequestDto request);

    CommonResponseDto verifyOtpToResetPassword(VerifyOtpRequestDto request);

    UserResponseDto resetPassword(ResetPasswordRequestDto request);
}
