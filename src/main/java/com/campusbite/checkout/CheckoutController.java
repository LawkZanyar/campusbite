package com.campusbite.checkout;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.campusbite.auth.CurrentUser;
import com.campusbite.auth.SessionUser;
import com.campusbite.common.BusinessException;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * The checkout page: choose a pickup time, add a note, place the order.
 */
@Controller
public class CheckoutController {

    private final CartService cartService;
    private final CheckoutService checkoutService;

    /** Constructor injection of both services. */
    public CheckoutController(CartService cartService, CheckoutService checkoutService) {
        this.cartService = cartService;
        this.checkoutService = checkoutService;
    }

    /** GET /checkout: order summary + pickup times. An empty cart goes back to /cart. */
    @GetMapping("/checkout")
    public String show(HttpSession session, Model model) {
        CurrentUser.require(session);
        Cart cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("form", new CheckoutForm());
        return showPage(cart, model);
    }

    /**
     * POST /checkout: place the order. On a problem (slot full, sold out, too late)
     * the message is shown on the same page and the cart is kept. On success the cart
     * is emptied and we redirect to the order page (Fatemeh's /orders/{id}).
     */
    @PostMapping("/checkout")
    public String placeOrder(@Valid @ModelAttribute("form") CheckoutForm form, BindingResult bindingResult,
                             HttpSession session, Model model) {
        SessionUser student = CurrentUser.require(session);
        Cart cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        if (bindingResult.hasErrors()) {
            return showPage(cart, model);
        }
        Long orderId;
        try {
            orderId = checkoutService.placeOrder(student, cart, form);
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return showPage(cart, model);
        }
        cart.clear();
        return "redirect:/orders/" + orderId + "?placed";
    }

    /** Helper: what the checkout page shows besides the form. */
    private String showPage(Cart cart, Model model) {
        model.addAttribute("cart", cart);
        model.addAttribute("slots", checkoutService.availableSlots());
        return "checkout/checkout";
    }
}
