package com.campusbite.menuadmin;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusbite.common.BusinessException;

/**
 * M4 Menu & Stock Management (owner: Pasha).
 * Admin pages to list, add, edit and delete menu items.
 * (Only admins will be let in from Week 10.)
 */
@Controller
public class MenuAdminController {

    private final MenuAdminService menuAdminService;

    /** Constructor injection of the service. */
    public MenuAdminController(MenuAdminService menuAdminService) {
        this.menuAdminService = menuAdminService;
    }

    /** GET /admin/items: table of all items. */
    @GetMapping("/admin/items")
    public String list(Model model) {
        model.addAttribute("items", menuAdminService.listItems());
        return "menuadmin/items";
    }

    /** GET /admin/items/new: empty form. itemId = null tells the template it is "new". */
    @GetMapping("/admin/items/new")
    public String showNewForm(Model model) {
        model.addAttribute("form", new ItemForm());
        return showForm(null, model);
    }

    /** POST /admin/items/new: validate and create. */
    @PostMapping("/admin/items/new")
    public String create(@Valid @ModelAttribute("form") ItemForm form, BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return showForm(null, model);
        }
        try {
            menuAdminService.createItem(form);
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return showForm(null, model);
        }
        return "redirect:/admin/items?created";
    }

    /** GET /admin/items/5/edit: the same form, filled with item 5. */
    @GetMapping("/admin/items/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", menuAdminService.getItemForm(id));
        return showForm(id, model);
    }

    /** POST /admin/items/5/edit: validate and save. */
    @PostMapping("/admin/items/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") ItemForm form,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showForm(id, model);
        }
        try {
            menuAdminService.updateItem(id, form);
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return showForm(id, model);
        }
        return "redirect:/admin/items?updated";
    }

    /**
     * POST /admin/items/5/delete. A POST (not a link) because it changes data.
     * A refused delete puts the message in a flash attribute, which survives
     * exactly one redirect.
     */
    @PostMapping("/admin/items/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            menuAdminService.deleteItem(id);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/items";
        }
        return "redirect:/admin/items?deleted";
    }

    /** Helper: what the form page needs besides the form itself. */
    private String showForm(Long itemId, Model model) {
        model.addAttribute("itemId", itemId);
        model.addAttribute("categories", menuAdminService.listCategories());
        return "menuadmin/item-form";
    }
}
