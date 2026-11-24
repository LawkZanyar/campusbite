package com.campusbite.slots;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.campusbite.common.BusinessException;

/**
 * M6 Pickup Slots (owner: Hananeh).
 * Admin page: list one day's slots and generate new ones.
 * (Only admins will be let in from Week 10.)
 */
@Controller
public class SlotAdminController {

    private final SlotService slotService;

    /** Constructor injection of the SlotService bean. */
    public SlotAdminController(SlotService slotService) {
        this.slotService = slotService;
    }

    /**
     * GET /admin/slots?date=2026-11-16
     * The date is optional; without it we show today. @DateTimeFormat tells Spring
     * the text is in ISO format (yyyy-MM-dd), which is what an input type="date" sends.
     */
    @GetMapping("/admin/slots")
    public String list(@RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       Model model) {
        if (date == null) {
            date = LocalDate.now();
        }
        SlotForm form = new SlotForm();
        form.setDate(date.isBefore(LocalDate.now()) ? LocalDate.now() : date);
        model.addAttribute("form", form);
        return showList(date, model);
    }

    /** POST /admin/slots: generate the slots of the chosen day. */
    @PostMapping("/admin/slots")
    public String generate(@Valid @ModelAttribute("form") SlotForm form, BindingResult bindingResult,
                           Model model) {
        LocalDate shownDate = (form.getDate() == null) ? LocalDate.now() : form.getDate();
        if (bindingResult.hasErrors()) {
            return showList(shownDate, model);
        }
        int created;
        try {
            created = slotService.generateForDate(form);
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return showList(shownDate, model);
        }
        return "redirect:/admin/slots?date=" + form.getDate() + "&created=" + created;
    }

    /** Helper: the slot table of one day. */
    private String showList(LocalDate date, Model model) {
        model.addAttribute("date", date);
        model.addAttribute("slots", slotService.listForDate(date));
        return "slots/list";
    }
}
