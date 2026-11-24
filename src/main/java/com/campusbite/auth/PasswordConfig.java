package com.campusbite.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * M1 Authentication & Accounts (owner: Lawk).
 * Creates ONE password encoder for the whole app. Any class that needs to hash
 * or check a password asks for a PasswordEncoder in its constructor.
 */
@Configuration
public class PasswordConfig {

    /** BCrypt: slow on purpose and salted, so a leaked database does not reveal passwords. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
