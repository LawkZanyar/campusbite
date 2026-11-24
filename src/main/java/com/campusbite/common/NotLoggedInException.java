package com.campusbite.common;

/**
 * SHARED FILE (owner: Lawk).
 * Thrown by CurrentUser.require(session) when a page needs a logged-in user
 * but nobody is logged in. GlobalExceptionHandler redirects to /login.
 */
public class NotLoggedInException extends RuntimeException {

    /** Creates the exception with a fixed message. */
    public NotLoggedInException() {
        super("Please log in first.");
    }
}
