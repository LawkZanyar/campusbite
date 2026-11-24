package com.campusbite.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
 * Register, log in and log out.
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

    /** GET /login shows the login form (with a success note after registering). */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("form", new LoginForm());
        return "auth/login";
    }

    /**
     * POST /login: check the email and password; on success remember the user in the
     * session. changeSessionId() gives the session a NEW id at login, so an id an
     * attacker planted before login becomes useless (session fixation).
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("form") LoginForm form, BindingResult bindingResult,
                        HttpServletRequest request, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        SessionUser user;
        try {
            user = authService.login(form.getEmail(), form.getPassword());
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/login";
        }
        HttpSession session = request.getSession();   // make sure a session exists
        request.changeSessionId();
        CurrentUser.store(session, user);
        return "redirect:/";
    }

    /** POST /logout: throw the whole session away (user, cart, ...) and go to the menu. */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/menu";
    }
}
