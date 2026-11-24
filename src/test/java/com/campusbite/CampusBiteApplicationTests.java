package com.campusbite;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * SHARED FILE (owner: Lawk).
 * Starts the whole Spring application once. If any bean is missing or
 * misconfigured (for example two classes for the same URL), this test fails.
 */
@SpringBootTest
class CampusBiteApplicationTests {

    @Test
    void contextLoads() {
        // nothing to do: the test passes if the application starts
    }
}
