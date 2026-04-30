package com.livehealth.shared.security;

import com.livehealth.auth.InvalidatedTokenRepository;
import com.livehealth.auth.JwtServiceImpl;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    JwtServiceImpl jwtService;

    @Inject
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Inject
    CurrentRequestUser currentRequestUser;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found in request header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

            if (invalidatedTokenRepository.existsById(jwtId)) {
                log.warn("Token invalidated: {}", jwtId);
                return;
            }

            String email = jwtService.extractEmail(token);
            if (email != null && !jwtService.isTokenExpired(token)) {
                Object userIdClaim = signedJWT.getJWTClaimsSet().getClaim("userId");
                if (userIdClaim != null) {
                    UUID userId = UUID.fromString(userIdClaim.toString());
                    currentRequestUser.setUserId(userId);
                    currentRequestUser.setEmail(email);

                    @SuppressWarnings("unchecked")
                    List<String> authorities = (List<String>) signedJWT.getJWTClaimsSet().getClaim("authorities");

                    SecurityContext originalContext = requestContext.getSecurityContext();
                    requestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return () -> email;
                        }

                        @Override
                        public boolean isUserInRole(String role) {
                            if (authorities == null) return false;
                            String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                            String roleWithoutPrefix = role.startsWith("ROLE_") ? role.substring(5) : role;
                            return authorities.contains(roleWithPrefix) || authorities.contains(roleWithoutPrefix);
                        }

                        @Override
                        public boolean isSecure() {
                            return originalContext.isSecure();
                        }

                        @Override
                        public String getAuthenticationScheme() {
                            return "Bearer";
                        }
                    });

                    log.debug("Authenticated user: {}, authorities: {}", email, authorities);
                }
            } else {
                log.warn("Invalid or expired token for email: {}", email);
            }
        } catch (Exception e) {
            log.warn("Failed to parse JWT token: {}", e.getMessage());
        }
    }
}
