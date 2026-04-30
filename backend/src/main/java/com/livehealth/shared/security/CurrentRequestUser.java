package com.livehealth.shared.security;

import jakarta.enterprise.context.RequestScoped;
import java.util.UUID;

@RequestScoped
public class CurrentRequestUser {
    private UUID userId;
    private String email;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
