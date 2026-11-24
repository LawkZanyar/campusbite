package com.campusbite.kitchen;

import com.campusbite.common.BusinessException;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * Thrown when someone tries a status change that StatusFlow does not allow,
 * e.g. READY back to PREPARING. It is a BusinessException, so controllers can
 * show its message on the page.
 */
public class InvalidStatusTransitionException extends BusinessException {

    /** @param message e.g. "Can't move order #12 from READY to PREPARING." */
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
