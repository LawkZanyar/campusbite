package com.campusbite.checkout;

import java.util.Locale;

/**
 * M5 Cart & Checkout (owner: Hananeh) - CONTRACT.
 * One row of order_items: which item, how many, and the price paid at order time.
 * We keep the price here because the menu price may change later.
 */
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long menuItemId;
    private String itemName;
    private int quantity;
    private int unitPriceIqd;

    /** Empty constructor for the repositories. */
    public OrderItem() {
    }

    /** Creates an order line for a new order (ids are set when it is saved). */
    public OrderItem(Long menuItemId, String itemName, int quantity, int unitPriceIqd) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPriceIqd = unitPriceIqd;
    }

    /** quantity x unit price. */
    public int getLineTotal() {
        return quantity * unitPriceIqd;
    }

    /** Line total as text, e.g. "5,000 IQD". */
    public String getLineTotalLabel() {
        return String.format(Locale.US, "%,d IQD", getLineTotal());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getUnitPriceIqd() { return unitPriceIqd; }
    public void setUnitPriceIqd(int unitPriceIqd) { this.unitPriceIqd = unitPriceIqd; }
}
