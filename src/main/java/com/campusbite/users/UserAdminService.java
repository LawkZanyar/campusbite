package com.campusbite.users;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.campusbite.common.BusinessException;

/**
 * M2 User & Role Administration (owner: Lawk).
 * What an admin can do with user accounts.
 */
@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Constructor injection of the repository and the BCrypt encoder. */
    public UserAdminService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Users whose name or email contains the search text; blank search = everybody. */
    public List<User> listUsers(String search) {
        return userRepository.findAll(search);
    }

    /**
     * Creates a KITCHEN or ADMIN account with a temporary password.
     * Rules: role must be KITCHEN or ADMIN (students register themselves),
     * and the email must not be used yet.
     *
     * @throws BusinessException if a rule is broken
     */
    public User createStaff(StaffForm form) {
        if (form.getRole() != Role.KITCHEN && form.getRole() != Role.ADMIN) {
            throw new BusinessException("Staff accounts must be KITCHEN or ADMIN.");
        }
        String email = form.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("An account with this email already exists.");
        }
        String hash = passwordEncoder.encode(form.getTemporaryPassword());
        User user = new User(form.getFullName().trim(), email, hash, form.getRole());
        return userRepository.save(user);
    }
}
