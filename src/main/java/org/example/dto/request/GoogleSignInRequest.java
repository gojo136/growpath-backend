package org.example.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class GoogleSignInRequest {

    @NotBlank(message = "ID token is required")
    private String idToken;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String profilePicture;

    // Constructors
    public GoogleSignInRequest() {}

    public GoogleSignInRequest(String idToken, String email, String fullName, String profilePicture) {
        this.idToken = idToken;
        this.email = email;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
    }

    // Getters and Setters
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "GoogleSignInRequest{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}