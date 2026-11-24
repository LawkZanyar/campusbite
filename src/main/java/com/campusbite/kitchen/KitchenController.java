package com.campusbite.kitchen;

import java.time.LocalDate;

import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusbite.auth.CurrentUser;
import com.campusbite.auth.SessionUser;
import com.campusbite.checkout.OrderStatus;
import com.campusbite.common.BusinessException;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * The kitchen board, the status buttons and the order history page for staff.
 */
@Controller
public class KitchenController {

    private final KitchenBoardService boardService;
    private final OrderStatusService orderStatusService;

    /** Constructor injection of my two services. */
    public KitchenController(KitchenBoardService boardService, OrderStatusService orderStatusService) {
        this.boardService = boardService;
        this.orderStatusService = orderStatusService;
    }

    /** GET /kitchen?date=2026-11-16 (date optional, default today). */
    @GetMapping("/kitchen")
    public String board(@RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        HttpSession session, Model model) {
        CurrentUser.require(session);
        if (date == null) {
            date = LocalDate.now();
        }
        model.addAttribute("date", date);
        model.addAttribute("board", boardService.getBoard(date));
        return "kitchen/board";
    }

    /**
     * POST /kitchen/orders/12/status with status=READY (one button on a card).
     * Spring converts the text "READY" into OrderStatus.READY.
     */
    @PostMapping("/kitchen/orders/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam OrderStatus status,
                               @RequestParam(required = false) String date,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser staff = CurrentUser.require(session);
        try {
            boardService.updateStatus(id, status, staff.getId());
            redirectAttributes.addFlashAttribute("message", "Order #" + id + " is now " + status + ".");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/kitchen" + (date == null ? "" : "?date=" + date);
    }

    /** GET /kitchen/orders/12: the order with its full status history. */
    @GetMapping("/kitchen/orders/{id}")
    public String orderDetail(@PathVariable Long id, HttpSession session, Model model) {
        CurrentUser.require(session);
        model.addAttribute("order", boardService.getOrder(id));
        model.addAttribute("history", orderStatusService.getHistory(id));
        return "kitchen/order";
    }
}
