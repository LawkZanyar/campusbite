package com.campusbite.auth;

import java.io.Serializable;

import com.campusbite.users.Role;

/**
 * M1 Authentication & Accounts (owner: Lawk) - CONTRACT used by every module.
 * The small object we keep in the HTTP session after login (attribute "user").
 * We do NOT put the whole User in the session, so the password hash never
 * sits in session memory. Serializable because sessions can be saved to disk.
 */
public class SessionUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long id;
    private String fullName;
    private final String email;
    private final Role role;

    /** Creates the session user from the values of a logged-in User. */
    public SessionUser(Long id, String fullName, String email, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    /** Changed when the user edits their name on the profile page. */
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }

    /** True for STUDENT accounts. Templates can write ${session.user.student}. */
    public boolean isStudent() { return role == Role.STUDENT; }

    /** True for KITCHEN accounts. */
    public boolean isKitchen() { return role == Role.KITCHEN; }

    /** True for ADMIN accounts. */
    public boolean isAdmin() { return role == Role.ADMIN; }

    /** True for KITCHEN or ADMIN: the people who work in the canteen. */
    public boolean isStaff() { return role == Role.KITCHEN || role == Role.ADMIN; }
}
