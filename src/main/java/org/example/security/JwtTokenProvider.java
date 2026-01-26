package org.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.security.SecureRandom;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final int MINIMUM_SECRET_BYTES = 32;
    
    @Value("${jwt.secret:}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;
    
    private final Environment environment;
    
    public JwtTokenProvider(Environment environment) {
        this.environment = environment;
    }
    
    @PostConstruct
    public void init() {
        // Check if secret is provided
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            // For development profiles, generate a random secret
            if (isDevelopmentProfile()) {
                jwtSecret = generateRandomSecret();
                logger.warn("JWT secret not provided. Generated random secret for development: {}", 
                        jwtSecret.substring(0, Math.min(20, jwtSecret.length())) + "...");
            } else {
                throw new IllegalStateException(
                        "JWT secret must be provided via 'jwt.secret' property. " +
                        "It cannot be empty and must be at least " + MINIMUM_SECRET_BYTES + " bytes (UTF-8) long.");
            }
        }
        
        // Validate secret length
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MINIMUM_SECRET_BYTES) {
            throw new IllegalStateException(
                    "JWT secret must be at least " + MINIMUM_SECRET_BYTES + " bytes (UTF-8). " +
                    "Current length: " + secretBytes.length + " bytes. " +
                    "Please provide a longer secret via 'jwt.secret' property.");
        }
        
        logger.info("JWT Token Provider initialized successfully with valid secret.");
    }
    
    private boolean isDevelopmentProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            activeProfiles = environment.getDefaultProfiles();
        }
        
        for (String profile : activeProfiles) {
            if ("dev".equalsIgnoreCase(profile) || "development".equalsIgnoreCase(profile) || "local".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
    
    private String generateRandomSecret() {
        // Generate 64 bytes of random data and encode as Base64
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(Long userId, String username, String email) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return Long.parseLong(claims.getSubject());
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("username", String.class);
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
    
    public Long getExpirationTime() {
        return jwtExpirationMs;
    }
}