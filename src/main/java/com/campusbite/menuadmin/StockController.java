package com.campusbite.menuadmin;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusbite.common.BusinessException;

/**
 * M4 Menu & Stock Management (owner: Pasha).
 * "Today's stock" page for kitchen staff and admins.
 */
@Controller
public class StockController {

    private final StockService stockService;

    /** Constructor injection of the StockService. */
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /** GET /kitchen/stock: one row per item with an input for "left today". */
    @GetMapping("/kitchen/stock")
    public String show(Model model) {
        model.addAttribute("items", stockService.listStock());
        return "menuadmin/stock";
    }

    /**
     * POST /kitchen/stock. @RequestParam Map receives EVERY form field as
     * name -> value, e.g. "left_3" -> "12", "soldOut_8" -> "on".
     */
    @PostMapping("/kitchen/stock")
    public String save(@RequestParam Map<String, String> form, RedirectAttributes redirectAttributes) {
        try {
            stockService.saveStock(form);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/kitchen/stock";
        }
        return "redirect:/kitchen/stock?saved";
    }

    /** POST /kitchen/stock/reset: every item back to its daily stock. */
    @PostMapping("/kitchen/stock/reset")
    public String reset() {
        stockService.resetAll();
        return "redirect:/kitchen/stock?reset";
    }
}
