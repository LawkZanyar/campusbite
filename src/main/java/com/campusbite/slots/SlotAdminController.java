package com.campusbite.slots;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * M6 Pickup Slots (owner: Hananeh).
 * Admin page that lists the pickup slots of one day.
 * (Only admins will be allowed here once access control arrives in Week 10.)
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
        model.addAttribute("date", date);
        model.addAttribute("slots", slotService.listForDate(date));
        return "slots/list";
    }
}
