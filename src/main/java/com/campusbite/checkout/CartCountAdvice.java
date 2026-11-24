package com.campusbite.checkout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * A @ModelAttribute method inside a @ControllerAdvice runs before EVERY controller
 * method and adds its result to the Model. So every page gets "cartCount", which
 * Lawk's nav bar shows as "Cart (n)".
 */
@ControllerAdvice
public class CartCountAdvice {

    /** Number of pieces in the cart, 0 when there is no session or no cart. */
    @ModelAttribute("cartCount")
    public int cartCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);   // false: don't create a session
        if (session == null) {
            return 0;
        }
        Cart cart = (Cart) session.getAttribute(CartService.SESSION_KEY);
        return (cart == null) ? 0 : cart.getItemCount();
    }
}
