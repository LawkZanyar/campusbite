package com.campusbite.auth;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.campusbite.common.BusinessException;

/**
 * M1 Authentication & Accounts (owner: Lawk).
 * Pages for creating an account. Log in / log out are added in Week 07.
 */
@Controller
public class AuthController {

    private final AuthService authService;

    /** Constructor injection: Spring passes the AuthService bean. */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** GET /register shows the empty register form. */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    /**
     * POST /register: validate the form, create the account, then redirect.
     * On any error we return the same page so the user sees the messages.
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            authService.register(form);
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
        // Post/Redirect/Get: a browser refresh will not submit the form twice
        return "redirect:/login?registered";
    }
}
