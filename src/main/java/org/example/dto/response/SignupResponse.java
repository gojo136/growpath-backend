package org.example.dto.response;

import java.time.LocalDateTime;

public class SignupResponse {

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String token;
    private String message;
    private LocalDateTime createdAt;

    // Constructors
    public SignupResponse() {}

    public SignupResponse(Long userId, String username, String email, String fullName,
                          String token, String message, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.token = token;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "SignupResponse{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", token='[REDACTED]'" +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}