package com.campusbite.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SHARED FILE (owner: Lawk).
 * Handles the home page. For now it shows a simple "CampusBite is running" page;
 * once the menu page exists (Week 07) it will redirect to /menu.
 */
@Controller
public class HomeController {

    /** GET / shows templates/index.html. */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
