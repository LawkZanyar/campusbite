package com.campusbite.common;

/**
 * SHARED FILE (owner: Lawk).
 * Thrown by any service when a record (order, menu item, user ...) does not exist.
 * GlobalExceptionHandler turns it into our friendly 404 page.
 */
public class NotFoundException extends RuntimeException {

    /** @param message text shown on the 404 page, e.g. "Menu item 99 not found" */
    public NotFoundException(String message) {
        super(message);
    }
}
