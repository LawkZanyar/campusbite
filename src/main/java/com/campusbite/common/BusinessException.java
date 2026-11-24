package com.campusbite.common;

/**
 * SHARED FILE (owner: Lawk).
 * Thrown by a service when a business rule is broken, for example
 * "An account with this email already exists" or "This slot is full".
 * Controllers catch it and show the message on the same page.
 * It is a RuntimeException, so inside an @Transactional method it also
 * makes Spring roll back every database change.
 */
public class BusinessException extends RuntimeException {

    /** @param message a sentence the user can read */
    public BusinessException(String message) {
        super(message);
    }
}
