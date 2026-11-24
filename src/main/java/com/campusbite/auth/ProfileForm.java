package com.campusbite.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * M1 Authentication & Accounts (owner: Lawk).
 * The editable part of "My profile": the full name. The email is shown but
 * cannot be changed, because it is the login name.
 */
public class ProfileForm {

    @NotBlank(message = "Please enter your name")
    @Size(max = 100, message = "Name can be at most 100 characters")
    private String fullName;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
