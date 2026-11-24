package com.campusbite.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * M1 Authentication & Accounts (owner: Lawk).
 * What the user types on the login page.
 */
public class LoginForm {

    @NotBlank(message = "Please enter your email")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Please enter your password")
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
