package com.livehealth.auth;

import com.livehealth.user.User;
import com.livehealth.shared.exception.InternalServerException;
import com.livehealth.auth.InvalidatedTokenRepository;
import com.livehealth.auth.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import lombok.experimental.NonFinal;
import io.quarkus.scheduler.Scheduled;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@ApplicationScoped
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtServiceImpl implements JwtService {

    @Inject
    @NonFinal
    @ConfigProperty(name = "jwt.secret")
    String secretKey;

    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public String generateToken(User user, long expirationTime) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + expirationTime))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("authorities", List.of("ROLE_" + user.getRole().name()))
                    .claim("userId", user.getId())
                    .claim("email", user.getEmail())
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS512),
                    claimsSet);

            signedJWT.sign(new MACSigner(secretKey.getBytes()));

            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new InternalServerException("Error while signing JWT");
        }
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public boolean isTokenValid(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email)) && !isTokenExpired(token);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredInvalidatedTokens() {
        Date now = new Date();
        invalidatedTokenRepository.deleteByExpiryTimeBefore(now);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}