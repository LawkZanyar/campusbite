package com.campusbite.checkout;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * Makes the 6-character pickup code the student shows at the counter (business rule 6).
 * SecureRandom, not Random: Random is predictable, so someone could guess other
 * students' codes. The alphabet has no 0/O/1/I, which are easy to misread.
 */
@Component
public class PickupCodeGenerator {

    /** 32 characters without 0, O, 1 and I. */
    static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    static final int LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    /** A new random code such as "K7P3QX". */
    public String next() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            code.append(ALPHABET.charAt(index));
        }
        return code.toString();
    }
}
