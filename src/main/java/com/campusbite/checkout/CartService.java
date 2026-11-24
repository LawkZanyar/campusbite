package com.campusbite.checkout;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.campusbite.menu.MenuItem;
import com.campusbite.menu.MenuService;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * Keeps one Cart per user in the HTTP session under the attribute "cart".
 */
@Service
public class CartService {

    /** Name of the session attribute that holds the Cart. */
    public static final String SESSION_KEY = "cart";

    private final MenuService menuService;

    /** Constructor injection of Pasha's MenuService (contract). */
    public CartService(MenuService menuService) {
        this.menuService = menuService;
    }

    /** The cart of this session; a new empty cart is created and stored the first time. */
    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(SESSION_KEY, cart);
        }
        return cart;
    }

    /**
     * Adds an item. We read the item from the menu (name, price, sold out) instead
     * of trusting anything else the browser sent except the id and the quantity.
     *
     * @return the item's name, for the "added" message
     */
    public String addToCart(HttpSession session, Long itemId, int qty) {
        MenuItem item = menuService.findById(itemId);
        getCart(session).add(item, qty);
        return item.getName();
    }

    /** Changes the quantity of one line (0 removes it). */
    public void updateQuantity(HttpSession session, Long itemId, int qty) {
        getCart(session).update(itemId, qty);
    }

    /** Removes one line. */
    public void removeFromCart(HttpSession session, Long itemId) {
        getCart(session).remove(itemId);
    }
}
