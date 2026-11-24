package com.campusbite.menuadmin;

import com.campusbite.common.BusinessException;

/**
 * M4 Menu & Stock Management (owner: Pasha) - CONTRACT used by checkout.
 * Thrown by StockService.decrement when there is not enough stock left today.
 * Because it is a RuntimeException, it rolls back the whole order in checkout.
 */
public class OutOfStockException extends BusinessException {

    private final Long itemId;

    /** @param itemId the item that ran out (checkout uses it to fix the cart) */
    public OutOfStockException(Long itemId, String itemName) {
        super("Sorry, \"" + itemName + "\" just sold out or has not enough left today.");
        this.itemId = itemId;
    }

    public Long getItemId() { return itemId; }
}
