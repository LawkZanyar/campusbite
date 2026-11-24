package com.campusbite.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SHARED FILE (owner: Lawk).
 * The home page of CampusBite is the menu, so "/" simply redirects there.
 */
@Controller
public class HomeController {

    /** GET / tells the browser to open /menu instead. */
    @GetMapping("/")
    public String home() {
        return "redirect:/menu";
    }
}
