package com.campusbite.users;

import java.util.List;
import java.util.Optional;

/**
 * M2 User & Role Administration (owner: Lawk) - CONTRACT used by every module.
 * Everything the app needs to read or save users. Two classes implement it:
 * InMemoryUserRepository (Phase 1, profile "memory") and JdbcUserRepository (MySQL, Week 08).
 * Services only depend on this interface, so swapping the storage changes no service code.
 */
public interface UserRepository {

    /** Finds a user by email (emails are stored in lower case). Empty if nobody has it. */
    Optional<User> findByEmail(String email);

    /** Finds a user by id. Empty if the id does not exist. */
    Optional<User> findById(Long id);

    /**
     * Lists users whose name or email contains the search text (case-insensitive),
     * sorted by role and then name. A null or blank search returns everybody.
     */
    List<User> findAll(String search);

    /** Inserts a new user, sets its generated id and returns it. */
    User save(User user);

    /** Updates name, email, password hash, role and active flag of an existing user. */
    void update(User user);

    /** Counts users with role ADMIN that are active (used by the "at least one admin" rule). */
    int countActiveAdmins();
}
