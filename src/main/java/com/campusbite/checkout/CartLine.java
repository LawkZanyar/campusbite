package com.campusbite.checkout;

import java.io.Serializable;
import java.util.Locale;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * One line of the cart: an item and how many of it. Kept in the HTTP session,
 * so it is Serializable.
 */
public class CartLine implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long menuItemId;
    private String name;
    private int unitPriceIqd;
    private int quantity;

    /** Creates a cart line. */
    public CartLine(Long menuItemId, String name, int unitPriceIqd, int quantity) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.unitPriceIqd = unitPriceIqd;
        this.quantity = quantity;
    }

    /** unit price x quantity, e.g. 2 x 2,500 = 5,000. */
    public int getLineTotal() {
        return unitPriceIqd * quantity;
    }

    /** The line total as text, e.g. "5,000 IQD". */
    public String getLineTotalLabel() {
        return String.format(Locale.US, "%,d IQD", getLineTotal());
    }

    /** The unit price as text. */
    public String getUnitPriceLabel() {
        return String.format(Locale.US, "%,d IQD", unitPriceIqd);
    }

    public Long getMenuItemId() { return menuItemId; }
    public String getName() { return name; }
    public int getUnitPriceIqd() { return unitPriceIqd; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
