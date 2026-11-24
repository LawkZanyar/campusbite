package com.campusbite.users;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * M2 User & Role Administration (owner: Lawk).
 * The "new staff account" form. The admin chooses KITCHEN or ADMIN and a temporary
 * password; the staff member changes it later on their profile page.
 */
public class StaffForm {

    @NotBlank(message = "Please enter the name")
    @Size(max = 100, message = "Name can be at most 100 characters")
    private String fullName;

    @NotBlank(message = "Please enter the email")
    @Email(message = "Please enter a valid email address")
    @Size(max = 120, message = "Email can be at most 120 characters")
    private String email;

    /** Spring turns the text "KITCHEN" from the select box into Role.KITCHEN. */
    @NotNull(message = "Please choose a role")
    private Role role;

    @NotBlank(message = "Please enter a temporary password")
    @Size(min = 8, max = 64, message = "Password must be 8 to 64 characters")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    private String temporaryPassword;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getTemporaryPassword() { return temporaryPassword; }
    public void setTemporaryPassword(String temporaryPassword) { this.temporaryPassword = temporaryPassword; }
}
