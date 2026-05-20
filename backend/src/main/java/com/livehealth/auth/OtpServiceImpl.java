package com.livehealth.auth;

import com.livehealth.shared.constant.CommonConstant;
import com.livehealth.auth.dto.request.otp.OtpRedisData;
import com.livehealth.auth.dto.request.otp.OtpType;
import com.livehealth.auth.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.livehealth.shared.base.RedisTemplate;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final RedisTemplate redisTemplate;

    private static final String OTP_KEY_PREFIX = "otp:";

    @Override
    public String generateOtp() {
        Random random = new Random();
        int otp = CommonConstant.OTP_MIN_VALUE + random.nextInt(
                CommonConstant.OTP_MAX_VALUE - CommonConstant.OTP_MIN_VALUE);
        return String.valueOf(otp);
    }

    @Override
    public void storeOtp(String email, String otp, OtpType type, String dataJson) {
        String key = buildKey(email, type);
        OtpRedisData otpData = OtpRedisData.builder()
                .otp(otp)
                .data(dataJson)
                .build();

        redisTemplate.opsForValue().set(key, otpData, CommonConstant.OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);

        log.debug("OTP stored in Redis for email: {}, type: {}, expires in {} minutes",
                email, type, CommonConstant.OTP_EXPIRATION_MINUTES);
    }

    @Override
    public boolean validateOtp(String email, String otp, OtpType type) {
        String key = buildKey(email, type);
        OtpRedisData otpData = (OtpRedisData) redisTemplate.opsForValue().get(key);

        if (otpData == null) {
            log.debug("No OTP found in Redis for email: {}, type: {}", email, type);
            return false;
        }

        boolean isValid = otp.equals(otpData.getOtp());
        log.debug("OTP validation result for email: {}, type: {}: {}", email, type, isValid);

        return isValid;
    }

    @Override
    public String getOtpData(String email, OtpType type) {
        String key = buildKey(email, type);
        OtpRedisData otpData = (OtpRedisData) redisTemplate.opsForValue().get(key);

        if (otpData == null) {
            return null;
        }

        return otpData.getData();
    }

    @Override
    public void clearOtp(String email, OtpType type) {
        String key = buildKey(email, type);
        redisTemplate.delete(key);
        log.debug("OTP cleared from Redis for email: {}, type: {}", email, type);
    }

    @Override
    public boolean isOtpValid(String email, OtpType type) {
        String key = buildKey(email, type);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String buildKey(String email, OtpType type) {
        return OTP_KEY_PREFIX + type.name().toLowerCase() + ":" + email;
    }
}
