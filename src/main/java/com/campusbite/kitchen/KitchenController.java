package com.campusbite.kitchen;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * The kitchen board page. Status buttons arrive in Week 07 (they need log in).
 */
@Controller
public class KitchenController {

    private final KitchenBoardService boardService;

    /** Constructor injection of the board service. */
    public KitchenController(KitchenBoardService boardService) {
        this.boardService = boardService;
    }

    /** GET /kitchen?date=2026-11-16 (date optional, default today). */
    @GetMapping("/kitchen")
    public String board(@RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        Model model) {
        if (date == null) {
            date = LocalDate.now();
        }
        model.addAttribute("date", date);
        model.addAttribute("board", boardService.getBoard(date));
        return "kitchen/board";
    }
}
