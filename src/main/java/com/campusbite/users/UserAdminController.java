package com.campusbite.users;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.campusbite.common.BusinessException;

/**
 * M2 User & Role Administration (owner: Lawk).
 * Admin pages for user accounts. (Only admins will be let in from Week 10.)
 */
@Controller
public class UserAdminController {

    /** The roles an admin may give to a new staff account. */
    private static final Role[] STAFF_ROLES = {Role.KITCHEN, Role.ADMIN};

    private final UserAdminService userAdminService;

    /** Constructor injection of the service. */
    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    /** GET /admin/users?q=text: the user list with a search box. */
    @GetMapping("/admin/users")
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("users", userAdminService.listUsers(q));
        model.addAttribute("q", q);
        return "users/list";
    }

    /** GET /admin/users/new: empty "new staff account" form. */
    @GetMapping("/admin/users/new")
    public String showNewForm(Model model) {
        model.addAttribute("form", new StaffForm());
        model.addAttribute("roles", STAFF_ROLES);
        return "users/new";
    }

    /** POST /admin/users/new: validate, create the account, back to the list. */
    @PostMapping("/admin/users/new")
    public String create(@Valid @ModelAttribute("form") StaffForm form, BindingResult bindingResult,
                         Model model) {
        model.addAttribute("roles", STAFF_ROLES);
        if (bindingResult.hasErrors()) {
            return "users/new";
        }
        try {
            userAdminService.createStaff(form);
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "users/new";
        }
        return "redirect:/admin/users?created";
    }
}
