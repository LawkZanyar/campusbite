package com.campusbite.users;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

/**
 * M2 User & Role Administration (owner: Lawk).
 * Phase 1 storage: keeps users in a Map in memory. Only created when the
 * "memory" profile is active. Everything is lost when the app restarts.
 * Starts with one demo user per role, all with the password Pass123!
 */
@Repository
@Profile("memory")
public class InMemoryUserRepository implements UserRepository {

    /** The demo password of the three starting users. */
    public static final String DEMO_PASSWORD = "Pass123!";

    private final Map<Long, User> users = new LinkedHashMap<>();
    private long nextId = 1;

    /** Creates the store with the three demo users (ids 1, 2, 3). */
    public InMemoryUserRepository() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(DEMO_PASSWORD);
        save(new User("Demo Student", "student@campusbite.test", hash, Role.STUDENT));
        save(new User("Demo Kitchen", "kitchen@campusbite.test", hash, Role.KITCHEN));
        save(new User("Demo Admin", "admin@campusbite.test", hash, Role.ADMIN));
    }

    @Override
    public synchronized Optional<User> findByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public synchronized List<User> findAll(String search) {
        List<User> result = new ArrayList<>();
        String text = (search == null) ? "" : search.trim().toLowerCase();
        for (User user : users.values()) {
            boolean matches = user.getFullName().toLowerCase().contains(text)
                    || user.getEmail().toLowerCase().contains(text);
            if (text.isEmpty() || matches) {
                result.add(user);
            }
        }
        // sort by role (STUDENT, KITCHEN, ADMIN order of the enum), then by name
        result.sort(Comparator.comparing(User::getRole).thenComparing(User::getFullName));
        return result;
    }

    @Override
    public synchronized User save(User user) {
        user.setId(nextId);
        nextId++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public synchronized void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public synchronized int countActiveAdmins() {
        int count = 0;
        for (User user : users.values()) {
            if (user.getRole() == Role.ADMIN && user.isActive()) {
                count++;
            }
        }
        return count;
    }
}
