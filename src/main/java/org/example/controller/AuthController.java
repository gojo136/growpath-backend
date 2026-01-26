package org.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.example.dto.request.LoginRequest;
import org.example.dto.response.LoginResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // TODO: Implement authentication logic
        LoginResponse response = new LoginResponse();
        response.setMessage("Login endpoint not implemented");
        return ResponseEntity.ok(response);
    }
}