package org.example.dto.request;

import jakarta.validation.constraints.*;

public class SignupRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 20, message = "Password must be 12-20 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{12,20}$", 
             message = "Password must contain uppercase, lowercase, number and special character")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotNull(message = "Age is required")
    @Min(value = 13, message = "Age must be at least 13")
    @Max(value = 120, message = "Age must not exceed 120")
    private Integer age;

    @Size(max = 100, message = "Profession must not exceed 100 characters")
    private String profession;

    @Size(max = 150, message = "Location must not exceed 150 characters")
    private String location;

    // Constructors
    public SignupRequest() {}

    public SignupRequest(String fullName, String email, String password, String confirmPassword,
                         Integer age, String profession, String location) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.age = age;
        this.profession = profession;
        this.location = location;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Validation helper method
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    @Override
    public String toString() {
        return "SignupRequest{" +
                "fullName='[REDACTED]'" +
                ", email='[REDACTED]'" +
                ", password='[REDACTED]'" +
                ", confirmPassword='[REDACTED]'" +
                ", age=" + age +
                ", profession='[REDACTED]'" +
                ", location='[REDACTED]'" +
                '}';
    }
}