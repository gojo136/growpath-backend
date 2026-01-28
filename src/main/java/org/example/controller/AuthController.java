package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.request.GoogleSignInRequest;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.SetPasswordRequest;
import org.example.dto.request.SignupRequest;
import org.example.dto.response.ApiResponse;
import org.example.dto.response.LoginResponse;
import org.example.dto.response.SignupResponse;
import org.example.entity.User;
import org.example.service.AuthService;
import org.example.validation.RegularSignup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://10.0.2.2:3000", "http://10.0.2.2:8081", "http://localhost:8082", "http://10.0.2.2:8082"}, allowCredentials = "true")
public class AuthController {
        
    @Autowired
    private AuthService authService;
    
    /** Public health check – no auth required. Use to verify backend is running. */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> health() {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("status", "UP");
        data.put("service", "SelfGrowth Backend");
        data.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(ApiResponse.success("Backend is running", data));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Validated(RegularSignup.class) @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    @PostMapping("/google-signin")
    public ResponseEntity<ApiResponse<LoginResponse>> googleSignIn(@Valid @RequestBody GoogleSignInRequest request) {
        LoginResponse response = authService.googleSignIn(request);
        return ResponseEntity.ok(ApiResponse.success("Google Sign-In processed", response));
    }
    
    @PostMapping("/set-password")
    public ResponseEntity<ApiResponse<LoginResponse>> setPassword(@Valid @RequestBody SetPasswordRequest request) {
        LoginResponse response = authService.setPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password set successfully", response));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = authService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User info retrieved", user));
    }
}