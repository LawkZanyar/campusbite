package com.campusbite.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * M1 Authentication & Accounts (owner: Lawk).
 * Holds what the user typed in the register form. Spring copies each input
 * (name="fullName", ...) into the field with the same name. The annotations
 * are checked when the controller parameter is marked @Valid.
 */
public class RegisterForm {

    @NotBlank(message = "Please enter your name")
    @Size(max = 100, message = "Name can be at most 100 characters")
    private String fullName;

    @NotBlank(message = "Please enter your email")
    @Email(message = "Please enter a valid email address")
    @Size(max = 120, message = "Email can be at most 120 characters")
    private String email;

    @NotBlank(message = "Please choose a password")
    @Size(min = 8, max = 64, message = "Password must be 8 to 64 characters")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    private String password;

    @NotBlank(message = "Please type the password again")
    private String confirmPassword;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
