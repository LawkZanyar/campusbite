package com.campusbite.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.campusbite.common.BusinessException;
import com.campusbite.users.Role;
import com.campusbite.users.User;
import com.campusbite.users.UserRepository;

/**
 * M1 Authentication & Accounts (owner: Lawk).
 * Business rules for accounts. Controllers never hash passwords or look up users
 * themselves; they call this service.
 */
@Service
public class AuthService {

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
}
