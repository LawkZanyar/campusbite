package com.campusbite.checkout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * The student's shopping cart, stored in the HTTP session until the order is placed.
 * Key = menu item id, value = the line for that item. A LinkedHashMap keeps the
 * lines in the order they were added. The methods that change the cart
 * (add, update, remove) come in Week 07.
 */
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Long, CartLine> lines = new LinkedHashMap<>();

    /** The lines in the order they were added (a copy, so callers can't change the map). */
    public List<CartLine> getLines() {
        return new ArrayList<>(lines.values());
    }

    /** True when there is nothing in the cart. */
    public boolean isEmpty() {
        return lines.isEmpty();
    }
}
