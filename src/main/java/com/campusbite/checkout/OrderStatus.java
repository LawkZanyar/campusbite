package com.campusbite.checkout;

/**
 * M5 Cart & Checkout (owner: Hananeh) - CONTRACT shared with the kitchen board (Fatemeh).
 * The life of an order. Which change is allowed (for example PLACED to PREPARING)
 * is decided by Fatemeh's kitchen.StatusFlow class, not here.
 */
public enum OrderStatus {
    /** The student has placed the order. */
    PLACED,
    /** The kitchen is cooking it. */
    PREPARING,
    /** Ready at the counter. */
    READY,
    /** The student picked it up. */
    COLLECTED,
    /** The student cancelled it (only possible while PLACED). */
    CANCELLED,
    /** Ready but never picked up. */
    NO_SHOW
}
