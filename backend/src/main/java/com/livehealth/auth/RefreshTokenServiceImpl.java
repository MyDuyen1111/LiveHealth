package com.livehealth.auth;

import com.livehealth.auth.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.livehealth.shared.base.RedisTemplate;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    @Override
    public void storeRefreshToken(String tokenId, String username, long ttlSeconds) {
        String tokenKey = buildTokenKey(tokenId);
        String userTokensKey = buildUserTokensKey(username);

        // Store token with username as value
        redisTemplate.opsForValue().set(tokenKey, username, ttlSeconds, TimeUnit.SECONDS);

        // Add token ID to user's token set
        redisTemplate.opsForSet().add(userTokensKey, tokenId);

        // Set expiration for user tokens set (slightly longer than token TTL)
        redisTemplate.expire(userTokensKey, ttlSeconds + 3600, TimeUnit.SECONDS);

        log.debug("Stored refresh token {} for user {} with TTL {} seconds", tokenId, username, ttlSeconds);
    }

    @Override
    public boolean isRefreshTokenValid(String tokenId) {
        String tokenKey = buildTokenKey(tokenId);
        Boolean exists = redisTemplate.hasKey(tokenKey);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public void invalidateRefreshToken(String tokenId) {
        String tokenKey = buildTokenKey(tokenId);

        // Get username before deleting
        String username = (String) redisTemplate.opsForValue().get(tokenKey);

        if (username != null) {
            // Remove from user's token set
            String userTokensKey = buildUserTokensKey(username);
            redisTemplate.opsForSet().remove(userTokensKey, tokenId);
        }

        // Delete the token
        redisTemplate.delete(tokenKey);

        log.debug("Invalidated refresh token {}", tokenId);
    }

    @Override
    public void invalidateAllUserTokens(String username) {
        String userTokensKey = buildUserTokensKey(username);

        // Get all token IDs for this user
        Set<Object> tokenIds = redisTemplate.opsForSet().members(userTokensKey);

        if (tokenIds != null && !tokenIds.isEmpty()) {
            // Delete all tokens
            for (Object tokenId : tokenIds) {
                String tokenKey = buildTokenKey((String) tokenId);
                redisTemplate.delete(tokenKey);
            }

            // Clear the user tokens set
            redisTemplate.delete(userTokensKey);

            log.warn("Invalidated all {} refresh tokens for user {} (potential security breach)",
                    tokenIds.size(), username);
        }
    }

    @Override
    public String getUsername(String tokenId) {
        String tokenKey = buildTokenKey(tokenId);
        return (String) redisTemplate.opsForValue().get(tokenKey);
    }

    private String buildTokenKey(String tokenId) {
        return REFRESH_TOKEN_PREFIX + tokenId;
    }

    private String buildUserTokensKey(String username) {
        return USER_TOKENS_PREFIX + username;
    }
}
