package com.campusbite.checkout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.campusbite.common.BusinessException;
import com.campusbite.menu.MenuItem;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * The student's shopping cart, stored in the HTTP session until the order is placed.
 * Key = menu item id, value = the line for that item. A LinkedHashMap keeps the
 * lines in the order they were added.
 * Business rule 5: at most 10 of one item and at most 30 items in the whole order.
 */
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Most pieces of ONE item per order. */
    public static final int MAX_PER_ITEM = 10;
    /** Most pieces in the whole order. */
    public static final int MAX_PER_ORDER = 30;

    private final Map<Long, CartLine> lines = new LinkedHashMap<>();

    /**
     * Adds qty of an item. If the item is already in the cart, the quantities are added up.
     *
     * @throws BusinessException if the item is sold out or a limit would be broken
     */
    public void add(MenuItem item, int qty) {
        if (item.isSoldOut()) {
            throw new BusinessException("\"" + item.getName() + "\" is sold out.");
        }
        CartLine existing = lines.get(item.getId());
        int alreadyInCart = (existing == null) ? 0 : existing.getQuantity();
        int newQty = alreadyInCart + qty;
        checkLimits(item.getId(), newQty);
        if (existing == null) {
            lines.put(item.getId(), new CartLine(item.getId(), item.getName(), item.getPriceIqd(), qty));
        } else {
            existing.setQuantity(newQty);
        }
    }

    /**
     * Sets the quantity of a line. 0 removes the line.
     *
     * @throws BusinessException if a limit would be broken
     */
    public void update(Long itemId, int qty) {
        if (qty == 0) {
            remove(itemId);
            return;
        }
        CartLine line = lines.get(itemId);
        if (line == null) {
            return;                       // nothing to update
        }
        checkLimits(itemId, qty);
        line.setQuantity(qty);
    }

    /** Removes the line of this item (if it is in the cart). */
    public void remove(Long itemId) {
        lines.remove(itemId);
    }

    /** Empties the cart (after the order is placed). */
    public void clear() {
        lines.clear();
    }

    /** Helper: rule 5 for a line that will have newQty pieces. */
    private void checkLimits(Long itemId, int newQty) {
        if (newQty < 1) {
            throw new BusinessException("Quantity must be at least 1.");
        }
        if (newQty > MAX_PER_ITEM) {
            throw new BusinessException("You can order at most " + MAX_PER_ITEM + " of one item.");
        }
        int otherLines = getItemCount();
        CartLine current = lines.get(itemId);
        if (current != null) {
            otherLines -= current.getQuantity();
        }
        if (otherLines + newQty > MAX_PER_ORDER) {
            throw new BusinessException("An order can have at most " + MAX_PER_ORDER + " items.");
        }
    }

    /** The lines in the order they were added (a copy, so callers can't change the map). */
    public List<CartLine> getLines() {
        return new ArrayList<>(lines.values());
    }

    /** True when there is nothing in the cart. */
    public boolean isEmpty() {
        return lines.isEmpty();
    }

    /** Number of pieces (sum of quantities), shown as "Cart (n)" in the nav bar. */
    public int getItemCount() {
        int count = 0;
        for (CartLine line : lines.values()) {
            count += line.getQuantity();
        }
        return count;
    }

    /** Sum of all line totals in IQD (prices as they were when added). */
    public int getTotalIqd() {
        int total = 0;
        for (CartLine line : lines.values()) {
            total += line.getLineTotal();
        }
        return total;
    }

    /** Total as text, e.g. "7,500 IQD". */
    public String getTotalLabel() {
        return String.format(Locale.US, "%,d IQD", getTotalIqd());
    }
}
