package com.campusbite.menu;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * M3 Menu Catalogue (owner: Pasha).
 * The public menu pages. Anyone can open them, logged in or not.
 */
@Controller
public class MenuController {

    private final MenuService menuService;

    /** Constructor injection of the MenuService bean. */
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * GET /menu?category=2&amp;veg=true
     * Both parameters are optional: no category = all categories, veg defaults to false.
     */
    @GetMapping("/menu")
    public String list(@RequestParam(required = false) Long category,
                       @RequestParam(defaultValue = "false") boolean veg,
                       Model model) {
        model.addAttribute("menu", menuService.getMenuGrouped(category, veg, null));
        model.addAttribute("categories", menuService.findAllCategories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("veg", veg);
        return "menu/list";
    }

    /**
     * GET /menu/items/5 - the detail page of one item.
     * {id} in the URL is copied into the id parameter by @PathVariable.
     * An unknown id makes findById throw NotFoundException, shown as our 404 page.
     */
    @GetMapping("/menu/items/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("item", menuService.findById(id));
        return "menu/detail";
    }
}
