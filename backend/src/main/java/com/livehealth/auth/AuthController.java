package com.livehealth.auth;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.auth.dto.request.*;
import com.livehealth.auth.dto.request.otp.VerifyOtpRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.auth.dto.response.LoginResponseDto;
import com.livehealth.auth.dto.response.TokenRefreshResponseDto;
import com.livehealth.user.dto.response.user.UserResponseDto;
import com.livehealth.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import com.livehealth.shared.base.HttpStatus;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RestApiV1
@Path("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @Operation(summary = "Đăng nhập tài khoản", description = "Dùng để đăng nhập tài khoản")
    @POST
    @Path(UrlConstant.Auth.LOGIN)
    public Response login(@Valid LoginRequestDto requestDto) {
        LoginResponseDto response = authService.authentication(requestDto);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Đăng xuất tài khoản", description = "Dùng để đăng xuất tài khoản")
    @POST
    @Path(UrlConstant.Auth.LOGOUT)
    public Response logout(@Valid LogoutRequestDto request) {
        CommonResponseDto response = authService.logout(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Làm mới token", description = "Dùng để cấp lại token")
    @POST
    @Path(UrlConstant.Auth.REFRESH_TOKEN)
    public Response refresh(
            @Valid TokenRefreshRequestDto request) {
        TokenRefreshResponseDto response = authService.refresh(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Đăng kí tài khoản", description = "Dùng để đăng kí tài khoản")
    @POST
    @Path(UrlConstant.Auth.REGISTER)
    public Response register(@Valid RegisterRequestDto requestDto) {
        CommonResponseDto response = authService.register(requestDto);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Quên mật khẩu", description = "Dùng để lấy lại mật khẩu")
    @POST
    @Path(UrlConstant.Auth.FORGOT_PASSWORD)
    public Response forgotPassword(
            @Valid ForgotPasswordRequestDto request) {
        CommonResponseDto response = authService.forgotPassword(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xác thực OTP", description = "Dùng để xác thực OTP sau khi yêu cầu lấy lại mật khẩu")
    @POST
    @Path(UrlConstant.Auth.VERIFY_OTP_TO_RESET_PASSWORD)
    public Response verifyToResetPassword(
            @Valid VerifyOtpRequestDto request) {
        CommonResponseDto response = authService.verifyOtpToResetPassword(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Đặt lại mật khẩu", description = "Dùng để đặt lại mật khẩu sau khi đã nhập được OTP")
    @POST
    @Path(UrlConstant.Auth.RESET_PASSWORD)
    public Response resetPassword(
            @Valid ResetPasswordRequestDto request) {
        UserResponseDto response = authService.resetPassword(request);
        return VsResponseUtil.success(response);
    }
}
