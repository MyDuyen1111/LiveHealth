package com.livehealth.auth;

import java.util.Date;
import com.livehealth.user.User;

public interface JwtService {

    String generateToken(User user, long expirationTime);

    String extractEmail(String token);

    Date extractExpiration(String token);

    boolean isTokenValid(String token, String email);

    boolean isTokenExpired(String token);

    String extractTokenId(String token);

}
