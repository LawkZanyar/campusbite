package com.campusbite.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.campusbite.common.BusinessException;
import com.campusbite.common.NotFoundException;
import com.campusbite.users.Role;
import com.campusbite.users.User;
import com.campusbite.users.UserRepository;

/**
 * M1 Authentication & Accounts (owner: Lawk).
 * Business rules for accounts: register, log in, edit my profile.
 * Controllers never hash passwords or look up users themselves; they call this service.
 */
@Service
public class AuthService {

    /** One message for every login failure (see login()). */
    static final String LOGIN_FAILED = "Invalid email or password.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Spring passes in the repository for the active profile and the BCrypt encoder. */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new STUDENT account.
     * Rules: both passwords must match and the email must not be used yet
     * (case-insensitive). The password is stored only as a BCrypt hash.
     *
     * @throws BusinessException with a readable message if a rule is broken
     */
    public User register(RegisterForm form) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new BusinessException("The two passwords do not match.");
        }
        String email = form.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("An account with this email already exists.");
        }
        String hash = passwordEncoder.encode(form.getPassword());
        User user = new User(form.getFullName().trim(), email, hash, Role.STUDENT);
        return userRepository.save(user);
    }

    /**
     * Checks an email + password and returns the small SessionUser to keep in the session.
     * Unknown email, wrong password and deactivated account all give the SAME message,
     * so an attacker cannot find out which emails have accounts.
     *
     * @throws BusinessException "Invalid email or password." on any failure
     */
    public SessionUser login(String email, String password) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BusinessException(LOGIN_FAILED));
        if (!user.isActive()) {
            throw new BusinessException(LOGIN_FAILED);
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(LOGIN_FAILED);
        }
        return new SessionUser(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }

    /**
     * Loads one user.
     *
     * @throws NotFoundException if the id does not exist
     */
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " was not found."));
    }

    /** Saves a new full name for the logged-in user (the profile page). */
    public void updateProfile(Long userId, ProfileForm form) {
        User user = getUser(userId);
        user.setFullName(form.getFullName().trim());
        userRepository.update(user);
    }
}
