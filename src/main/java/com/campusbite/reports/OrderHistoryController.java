package com.campusbite.reports;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusbite.auth.CurrentUser;
import com.campusbite.auth.SessionUser;
import com.campusbite.checkout.Order;
import com.campusbite.common.BusinessException;

/**
 * M8 Order History & Reports (owner: Fatemeh).
 * "My orders", the order detail page with the pickup code, and cancel.
 */
@Controller
public class OrderHistoryController {

    private final OrderHistoryService historyService;

    /** Constructor injection of the service. */
    public OrderHistoryController(OrderHistoryService historyService) {
        this.historyService = historyService;
    }

    /** GET /orders: the logged-in student's orders. */
    @GetMapping("/orders")
    public String myOrders(HttpSession session, Model model) {
        SessionUser user = CurrentUser.require(session);
        model.addAttribute("orders", historyService.findMyOrders(user.getId()));
        return "reports/my-orders";
    }

    /** GET /orders/12: one order (owner or staff only; others get 404). */
    @GetMapping("/orders/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        SessionUser user = CurrentUser.require(session);
        Order order = historyService.getOrderForViewer(user, id);
        model.addAttribute("order", order);
        model.addAttribute("timeline", historyService.getTimeline(id));
        model.addAttribute("isOwner", order.getUserId().equals(user.getId()));
        return "reports/order-detail";
    }

    /** POST /orders/12/cancel: cancel while PLACED, then back to the detail page. */
    @PostMapping("/orders/{id}/cancel")
    public String cancel(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = CurrentUser.require(session);
        try {
            historyService.cancel(user, id);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/orders/" + id;
        }
        return "redirect:/orders/" + id + "?cancelled";
    }
}
