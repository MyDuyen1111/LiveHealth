package com.livehealth.auth;

import com.livehealth.shared.constant.*;
import com.livehealth.auth.dto.request.*;
import com.livehealth.auth.dto.request.otp.*;
import com.livehealth.shared.dto.*;
import com.livehealth.auth.dto.response.*;
import com.livehealth.user.dto.response.user.*;
import com.livehealth.auth.InvalidatedToken;
import com.livehealth.user.*;
import com.livehealth.auth.AuthMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.auth.InvalidatedTokenRepository;
import com.livehealth.user.UserRepository;
import com.livehealth.auth.*;
import com.livehealth.shared.utils.TimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import lombok.experimental.NonFinal;
import com.livehealth.shared.base.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.text.ParseException;
import java.util.*;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    AuthMapper authMapper;
    JwtService jwtService;
    EmailService emailService;

    // New injected services
    OtpService otpService;
    PasswordEncoder passwordEncoder;
    RefreshTokenService refreshTokenService;

    ObjectMapper objectMapper;

    @Inject
    @NonFinal
    @ConfigProperty(name = "jwt.access.expiration_time")
    long ACCESS_TOKEN_EXPIRATION;

    @Inject
    @NonFinal
    @ConfigProperty(name = "jwt.refresh.expiration_time")
    long REFRESH_TOKEN_EXPIRATION;

    @Override
    public LoginResponseDto authentication(LoginRequestDto request) {
        String email = request.getEmail().trim();
        String password = request.getPassword().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new VsException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorMessage.Auth.ERR_INVALID_CREDENTIALS));

        // Validate password
        boolean auth = passwordEncoder.matches(password, user.getPassword());
        if (!auth) {
            throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.ERR_INVALID_CREDENTIALS);
        }

        // Generate tokens
        String accessToken = jwtService.generateToken(user, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = jwtService.generateToken(user, REFRESH_TOKEN_EXPIRATION);

        // Store refresh token in Redis
        String refreshTokenId = jwtService.extractTokenId(refreshToken);
        refreshTokenService.storeRefreshToken(refreshTokenId, user.getEmail(), REFRESH_TOKEN_EXPIRATION / 1000);

        return LoginResponseDto.builder()
                .status(HttpStatus.OK)
                .message(SuccessMessage.Auth.LOGIN_SUCCESS)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .tokenType(CommonConstant.BEARER_TOKEN)
                .build();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto logout(LogoutRequestDto request) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(request.getToken());

            String email = jwtService.extractEmail(request.getToken());
            if (!userRepository.existsUserByEmail(email)) {
                throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.User.ERR_USER_NOT_EXISTED);
            }

            if (!jwtService.isTokenValid(request.getToken(), email)) {
                throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.ERR_TOKEN_INVALIDATED);
            }

            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            if (invalidatedTokenRepository.existsById(jwtId)) {
                throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.ERR_TOKEN_ALREADY_INVALIDATED);
            }

            // Invalidate access token
            invalidatedTokenRepository.save(new InvalidatedToken(jwtId, expirationTime));

            // Invalidate all refresh tokens for this user
            refreshTokenService.invalidateAllUserTokens(email);

            return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Auth.LOGOUT_SUCCESS);
        } catch (ParseException e) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.ERR_GET_TOKEN_CLAIM_SET_FAIL);
        }
    }

    @Override
    public TokenRefreshResponseDto refresh(TokenRefreshRequestDto request) {
        String refreshToken = request.getRefreshToken();
        String refreshTokenId = jwtService.extractTokenId(refreshToken);
        String email = jwtService.extractEmail(refreshToken);

        if (!userRepository.existsUserByEmail(email)) {
            throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.User.ERR_USER_NOT_EXISTED);
        }

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.EXPIRED_REFRESH_TOKEN);
        }

        if (!jwtService.isTokenValid(refreshToken, email)) {
            throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.INVALID_REFRESH_TOKEN);
        }

        // Check if refresh token exists in Redis (not yet used)
        if (!refreshTokenService.isRefreshTokenValid(refreshTokenId)) {
            // Token reuse detected! Invalidate all tokens for this user
            log.warn("Refresh token reuse detected for user: {}. Invalidating all tokens.", email);
            refreshTokenService.invalidateAllUserTokens(email);
            throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.INVALID_REFRESH_TOKEN);
        }

        // Invalidate the old refresh token immediately (rotation)
        refreshTokenService.invalidateRefreshToken(refreshTokenId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new VsException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorMessage.User.ERR_USER_NOT_EXISTED));

        // Generate NEW access token and NEW refresh token
        String newAccessToken = jwtService.generateToken(user, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtService.generateToken(user, REFRESH_TOKEN_EXPIRATION);

        // Store the new refresh token in Redis
        String newRefreshTokenId = jwtService.extractTokenId(newRefreshToken);
        refreshTokenService.storeRefreshToken(newRefreshTokenId, user.getEmail(), REFRESH_TOKEN_EXPIRATION / 1000);

        return TokenRefreshResponseDto.builder()
                .tokenType(CommonConstant.BEARER_TOKEN)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto register(RegisterRequestDto request) {

        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_EMAIL_EXISTED);
        }

        if (!request.getPassword().equals(request.getReEnterPassword())) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_RE_ENTER_PASSWORD_NOT_MATCH);
        }

        User user = authMapper.registerRequestDtoToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(TimeUtil.today());

        userRepository.save(user);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Auth.REGISTER_SUCCESS);
    }

    @Override
    public CommonResponseDto forgotPassword(ForgotPasswordRequestDto request) {
        if (!userRepository.existsUserByEmail(request.getEmail())) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_EMAIL_NOT_EXISTED);
        }

        String otp = otpService.generateOtp();

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            otpService.storeOtp(request.getEmail(), otp, OtpType.PASSWORD_RESET, requestJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize forgot password request", e);
            throw new VsException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.ERR_EXCEPTION_GENERAL);
        }

        try {
            emailService.sendOtpEmail(request.getEmail(), otp);
        } catch (RuntimeException e) {
            otpService.clearOtp(request.getEmail(), OtpType.PASSWORD_RESET);
            throw e;
        }

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Auth.SUCCESS_SEND_OTP);
    }

    @Override
    public CommonResponseDto verifyOtpToResetPassword(VerifyOtpRequestDto request) {

        if (!otpService.validateOtp(request.getEmail(), request.getOtp(), OtpType.PASSWORD_RESET)) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Auth.ERR_OTP_INVALID);
        }

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Auth.SUCCESS_VERIFY_OTP);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserResponseDto resetPassword(ResetPasswordRequestDto request) {
        if (!userRepository.existsUserByEmail(request.getEmail())) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_EMAIL_NOT_EXISTED);
        }

        if (!request.getNewPassword().equals(request.getReEnterPassword())) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_RE_ENTER_PASSWORD_NOT_MATCH);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.User.ERR_DUPLICATE_OLD_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpService.clearOtp(request.getEmail(), OtpType.PASSWORD_RESET);

        return authMapper.userToUserResponseDto(user);
    }
}
