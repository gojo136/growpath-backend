package org.example.dto.response;

import java.time.LocalDateTime;

public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn; // in milliseconds
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String authId;
    private String message;
    private LocalDateTime loginTime;
    private boolean firstTimeGoogleUser = false;

    // Constructors
    public LoginResponse() {
        this.loginTime = LocalDateTime.now();
    }

    public LoginResponse(String token, Long expiresIn, Long userId, String username,
                         String email, String fullName, String authId, String message) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.authId = authId;
        this.message = message;
        this.loginTime = LocalDateTime.now();
        this.firstTimeGoogleUser = false;
    }

    public LoginResponse(String token, Long expiresIn, Long userId, String username,
                         String email, String fullName, String authId, String message, boolean firstTimeGoogleUser) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.authId = authId;
        this.message = message;
        this.loginTime = LocalDateTime.now();
        this.firstTimeGoogleUser = firstTimeGoogleUser;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

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

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public boolean isFirstTimeGoogleUser() {
        return firstTimeGoogleUser;
    }

    public void setFirstTimeGoogleUser(boolean firstTimeGoogleUser) {
        this.firstTimeGoogleUser = firstTimeGoogleUser;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='[REDACTED]'" +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", message='" + message + '\'' +
                ", loginTime=" + loginTime +
                '}';
    }
}