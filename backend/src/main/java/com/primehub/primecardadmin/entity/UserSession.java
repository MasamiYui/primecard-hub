package com.primehub.primecardadmin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @Column(length = 64)
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public UserSession() {}

    public UserSession(String sessionId, User user, String token, String refreshToken, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.sessionId = sessionId;
        this.user = user;
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void refresh(String newToken, String newRefreshToken, LocalDateTime newExpiresAt) {
        this.token = newToken;
        this.refreshToken = newRefreshToken;
        this.expiresAt = newExpiresAt;
    }

    public void invalidate() {
        this.expiresAt = LocalDateTime.now();
    }
}