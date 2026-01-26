package org.example.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SetPasswordRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 20, message = "Password must be 12-20 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{12,20}$", 
             message = "Password must contain uppercase, lowercase, number and special character")
    private String password;
    
    @NotBlank(message = "Google ID is required")
    private String googleId;
    
    @NotBlank(message = "Display name is required")
    private String displayName;
    
    private String photoUrl;

    public SetPasswordRequest() {}

    public SetPasswordRequest(String email, String password, String googleId, String displayName, String photoUrl) {
        this.email = email;
        this.password = password;
        this.googleId = googleId;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}