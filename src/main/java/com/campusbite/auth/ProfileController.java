package com.campusbite.auth;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.campusbite.users.User;

/**
 * M1 Authentication & Accounts (owner: Lawk).
 * "My profile": every logged-in user can see their account and edit their name.
 */
@Controller
public class ProfileController {

    private final AuthService authService;

    /** Constructor injection of the AuthService bean. */
    public ProfileController(AuthService authService) {
        this.authService = authService;
    }

    /** GET /profile: the form filled with the current name. Not logged in -> /login. */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        SessionUser current = CurrentUser.require(session);
        User user = authService.getUser(current.getId());
        ProfileForm form = new ProfileForm();
        form.setFullName(user.getFullName());
        model.addAttribute("form", form);
        model.addAttribute("account", user);
        return "auth/profile";
    }

    /** POST /profile: validate and save the new name, also in the session (for the nav bar). */
    @PostMapping("/profile")
    public String saveProfile(@Valid @ModelAttribute("form") ProfileForm form, BindingResult bindingResult,
                              HttpSession session, Model model) {
        SessionUser current = CurrentUser.require(session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("account", authService.getUser(current.getId()));
            return "auth/profile";
        }
        authService.updateProfile(current.getId(), form);
        current.setFullName(form.getFullName().trim());
        return "redirect:/profile?saved";
    }
}
