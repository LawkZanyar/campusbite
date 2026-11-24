package com.campusbite.common;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SHARED FILE (owner: Lawk).
 * Gives the whole app one java.time.Clock bean. Services that need "now"
 * (checkout cut-off, NO_SHOW rule, reports) ask for a Clock in their constructor
 * and call LocalDateTime.now(clock). Tests can pass a fixed clock instead,
 * so time-based rules can be tested at any time of day.
 */
@Configuration
public class ClockConfig {

    /** The real system clock in the computer's time zone. */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
