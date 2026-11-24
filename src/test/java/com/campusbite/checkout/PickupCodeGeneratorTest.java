package com.campusbite.checkout;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * The pickup code is 6 characters from the safe alphabet (no 0, O, 1, I).
 */
class PickupCodeGeneratorTest {

    private final PickupCodeGenerator generator = new PickupCodeGenerator();

    @Test
    void codeHasSixAllowedCharacters() {
        String code = generator.next();

        assertThat(code).hasSize(6);
        for (char c : code.toCharArray()) {
            assertThat(PickupCodeGenerator.ALPHABET).contains(String.valueOf(c));
        }
    }

    @Test
    void thousandCodesNeverContainConfusingCharacters() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String code = generator.next();
            assertThat(code).doesNotContain("0", "O", "1", "I");
            codes.add(code);
        }
        // 32^6 = about one billion possible codes, so 1000 codes are (almost) all different
        assertThat(codes.size()).isGreaterThan(990);
    }
}
