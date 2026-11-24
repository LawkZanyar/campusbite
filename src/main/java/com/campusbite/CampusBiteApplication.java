package com.campusbite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SHARED FILE (owner: Lawk).
 * Entry point of CampusBite. @SpringBootApplication turns on component scanning for
 * every package under com.campusbite (auth, users, menu, ...) and Spring Boot's
 * auto-configuration (embedded Tomcat, Spring MVC, Thymeleaf).
 */
@SpringBootApplication
public class CampusBiteApplication {

    /** Starts the embedded Tomcat server on port 8080. */
    public static void main(String[] args) {
        SpringApplication.run(CampusBiteApplication.class, args);
    }
}
