package com.campusbite.checkout;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusbite.auth.CurrentUser;
import com.campusbite.common.BusinessException;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * Cart pages. You must be logged in to use the cart.
 */
@Controller
public class CartController {

    private final CartService cartService;

    /** Constructor injection of the CartService. */
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * POST /cart/add from the item page (Pasha's form sends itemId and quantity).
     * The message travels to the item page as a flash attribute: it is kept in the
     * session for exactly one redirect, then removed.
     */
    @PostMapping("/cart/add")
    public String add(@RequestParam Long itemId, @RequestParam(defaultValue = "1") int quantity,
                      HttpSession session, RedirectAttributes redirectAttributes) {
        CurrentUser.require(session);
        try {
            String name = cartService.addToCart(session, itemId, quantity);
            redirectAttributes.addFlashAttribute("cartMessage", quantity + " x " + name + " added to your cart.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
        }
        return "redirect:/menu/items/" + itemId;
    }

    /** GET /cart: the cart page. */
    @GetMapping("/cart")
    public String show(HttpSession session, Model model) {
        CurrentUser.require(session);
        model.addAttribute("cart", cartService.getCart(session));
        return "checkout/cart";
    }

    /** POST /cart/update: new quantity for one line (0 removes it). */
    @PostMapping("/cart/update")
    public String update(@RequestParam Long itemId, @RequestParam int quantity,
                         HttpSession session, RedirectAttributes redirectAttributes) {
        CurrentUser.require(session);
        try {
            cartService.updateQuantity(session, itemId, quantity);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    /** POST /cart/remove: removes one line. */
    @PostMapping("/cart/remove")
    public String remove(@RequestParam Long itemId, HttpSession session) {
        CurrentUser.require(session);
        cartService.removeFromCart(session, itemId);
        return "redirect:/cart";
    }
}
