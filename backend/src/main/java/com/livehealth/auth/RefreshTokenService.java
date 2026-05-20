package com.livehealth.auth;

public interface RefreshTokenService {

    void storeRefreshToken(String tokenId, String username, long ttlSeconds);

    boolean isRefreshTokenValid(String tokenId);

    void invalidateRefreshToken(String tokenId);

    void invalidateAllUserTokens(String username);

    String getUsername(String tokenId);
}
