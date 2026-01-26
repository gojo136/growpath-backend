package org.example.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.example.dto.request.GoogleSignInRequest;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.SetPasswordRequest;
import org.example.dto.response.LoginResponse;
import org.example.dto.request.SignupRequest;
import org.example.dto.response.SignupResponse;
import org.example.entity.User;
import org.example.exception.EmailAlreadyExistsException;
import org.example.repository.UserRepository;
import org.example.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoogleTokenVerificationService googleTokenVerificationService;

    /**
     * Register a new user
     * @param request SignupRequest containing user details
     * @return SignupResponse with user info and JWT token
     */
    public SignupResponse signup(SignupRequest request) {
        logger.info("Processing signup request for email: {}", request.getEmail());

        // Validate password confirmation
        if (request.getPassword() == null || request.getConfirmPassword() == null) {
            throw new IllegalArgumentException("Password and confirmation are required");
        }

        if (!request.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Normalize and validate email
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        // Check if email already exists
        if (userRepository.existsByEmail(normalizedEmail)) {
            logger.warn("Signup attempt with existing email: {}", normalizedEmail);
            throw new EmailAlreadyExistsException("Email already registered. Please login or use a different email.");
        }

        // Generate username from email (before @ symbol)
        String username = generateUsernameFromEmail(normalizedEmail);

        // Ensure username is unique
        username = ensureUniqueUsername(username);

        // Hash password using BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create user entity with auth_id
        User user = new User(
                username,
                normalizedEmail,
                hashedPassword,
                request.getFullName().trim(),
                request.getAge(),
                request.getProfession() != null ? request.getProfession().trim() : null,
                request.getLocation() != null ? request.getLocation().trim() : null
        );
        
        // Set hasPassword flag
        user.setHasPassword(true);
        
        // Generate auth_id (UUID format)
        user.setAuthId(java.util.UUID.randomUUID());

        // Save user to database
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );

        // Return response with token
        SignupResponse response = new SignupResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                token,
                "User registered successfully. Welcome to GrowPath!",
                savedUser.getCreatedAt()
        );

        return response;
    }

    /**
     * Authenticate user and generate JWT token
     * @param request LoginRequest containing email and password
     * @return LoginResponse with JWT token and user info
     */
    public LoginResponse login(LoginRequest request) {
        logger.info("Processing login request for email: {}", request.getEmail());

        // Normalize email
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        // Find user by email
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    logger.warn("Login attempt with non-existent email: {}", normalizedEmail);
                    return new BadCredentialsException("Invalid email or password");
                });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Invalid password attempt for email: {}", normalizedEmail);
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );

        Long expiresIn = jwtTokenProvider.getExpirationTime();

        logger.info("User logged in successfully: {}", user.getUsername());

        // Return response
        LoginResponse response = new LoginResponse(
                token,
                expiresIn,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getAuthId() != null ? user.getAuthId().toString() : null,
                "Login successful. Welcome back!"
        );

        return response;
    }

    /**
     * Handle Google Sign-In - Check if user needs password setup
     * @param request GoogleSignInRequest containing Google ID token
     * @return LoginResponse indicating if password setup is needed
     */
    public LoginResponse googleSignIn(GoogleSignInRequest request) {
        logger.info("Processing Google sign-in request");
        
        try {
            // Verify Google ID token
            GoogleIdToken.Payload payload = googleTokenVerificationService.verifyToken(request.getIdToken());
            
            // Extract verified user information
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            Boolean emailVerified = payload.getEmailVerified();
            
            // Security check: ensure email is verified by Google
            if (!Boolean.TRUE.equals(emailVerified)) {
                throw new SecurityException("Google account email is not verified");
            }
            
            // Validate that request data matches token data
            if (!email.equalsIgnoreCase(request.getEmail().trim())) {
                throw new SecurityException("Email mismatch between token and request");
            }
            
            String normalizedEmail = email.toLowerCase().trim();
            
            // Check if user already exists
            User user = userRepository.findByEmail(normalizedEmail).orElse(null);
            
            if (user == null || !Boolean.TRUE.equals(user.getHasPassword())) {
                // New user or existing user without password - needs password setup
                return new LoginResponse(
                    null, null, null, null, email, name, null,
                    "Password setup required", true
                );
            } else {
                // Existing user with password - regular login
                String token = jwtTokenProvider.generateToken(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                );
                
                Long expiresIn = jwtTokenProvider.getExpirationTime();
                
                return new LoginResponse(
                        token, expiresIn, user.getId(), user.getUsername(),
                        user.getEmail(), user.getFullName(),
                        user.getAuthId() != null ? user.getAuthId().toString() : null,
                        "Google Sign-In successful. Welcome back!", false
                );
            }
            
        } catch (SecurityException e) {
            logger.error("Google sign-in security error: {}", e.getMessage());
            throw new BadCredentialsException("Google sign-in failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Google sign-in error: {}", e.getMessage());
            throw new RuntimeException("Google sign-in failed. Please try again.");
        }
    }

    /**
     * Set password for Google user and complete account setup
     * @param request SetPasswordRequest containing password and Google info
     * @return LoginResponse with JWT token
     */
    public LoginResponse setPassword(SetPasswordRequest request) {
        logger.info("Processing set password request for email: {}", request.getEmail());
        
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        
        // Check if user already exists
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        
        if (user == null) {
            // Create new user
            String username = generateUsernameFromEmail(normalizedEmail);
            username = ensureUniqueUsername(username);
            
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            
            user = new User(
                    username,
                    normalizedEmail,
                    hashedPassword,
                    request.getDisplayName().trim(),
                    null, null, null
            );
            
            user.setGoogleId(request.getGoogleId());
            user.setPhotoUrl(request.getPhotoUrl());
            user.setHasPassword(true);
            // Generate auth_id
            user.setAuthId(java.util.UUID.randomUUID());
            
            user = userRepository.save(user);
            logger.info("New Google user created with password: {}", user.getId());
            
        } else {
            // Update existing user with password and Google info
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            
            user.setPassword(hashedPassword);
            user.setGoogleId(request.getGoogleId());
            user.setPhotoUrl(request.getPhotoUrl());
            user.setFullName(request.getDisplayName().trim());
            user.setHasPassword(true);
            // Generate auth_id if not exists
            if (user.getAuthId() == null) {
                user.setAuthId(java.util.UUID.randomUUID());
            }
            
            user = userRepository.save(user);
            logger.info("Existing user updated with password: {}", user.getId());
        }
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
        
        Long expiresIn = jwtTokenProvider.getExpirationTime();
        
        return new LoginResponse(
                token, expiresIn, user.getId(), user.getUsername(),
                user.getEmail(), user.getFullName(),
                user.getAuthId() != null ? user.getAuthId().toString() : null,
                "Account setup completed successfully!", false
        );
    }

    /**
     * Get user by ID
     * @param userId User ID
     * @return User entity
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    /**
     * Get user by email
     * @param email User email
     * @return User entity
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Generate username from email (part before @)
     * @param email User email
     * @return Generated username
     */
    private String generateUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        int atIndex = email.indexOf('@');
        if (atIndex == 0) {
            throw new IllegalArgumentException("Invalid email format");
        }

        String baseUsername = email.substring(0, atIndex).toLowerCase();

        // Remove special characters and keep only alphanumeric and underscore
        baseUsername = baseUsername.replaceAll("[^a-z0-9_]", "");

        // Ensure username is not empty after cleaning
        if (baseUsername.isEmpty()) {
            baseUsername = "user";
        }

        // Ensure minimum length of 3 characters
        if (baseUsername.length() < 3) {
            baseUsername = baseUsername + "user";
        }

        return baseUsername;
    }

    /**
     * Ensure username is unique by appending numbers if needed
     * @param baseUsername Base username
     * @return Unique username
     */
    private String ensureUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;
        final int maxAttempts = 1000;

        while (userRepository.existsByUsername(username) && counter <= maxAttempts) {
            username = baseUsername + counter;
            counter++;
        }

        if (counter > maxAttempts) {
            logger.error("Failed to generate unique username after {} attempts", maxAttempts);
            throw new RuntimeException("Unable to generate unique username. Please try again later.");
        }

        logger.debug("Generated unique username: {}", username);
        return username;
    }

    /**
     * Validate if email exists
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email.toLowerCase().trim());
    }
}