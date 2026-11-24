package com.campusbite.auth;

import javax.servlet.http.HttpSession;

import com.campusbite.common.NotLoggedInException;

/**
 * M1 Authentication & Accounts (owner: Lawk) - CONTRACT used by every module.
 * Small helper to read the logged-in user from the HTTP session.
 * Teammates call CurrentUser.require(session) in a controller to get the user's id and role.
 * All methods are static, so nobody needs to inject anything.
 */
public final class CurrentUser {

    /** Name of the session attribute that holds the SessionUser. */
    public static final String SESSION_KEY = "user";

    private CurrentUser() {
        // only static methods, no objects
    }

    /** Returns the logged-in user, or null when nobody is logged in. */
    public static SessionUser get(HttpSession session) {
        Object value = session.getAttribute(SESSION_KEY);
        if (value instanceof SessionUser) {
            return (SessionUser) value;
        }
        return null;
    }

    /**
     * Returns the logged-in user, or throws NotLoggedInException when nobody is
     * logged in (GlobalExceptionHandler then redirects to /login).
     */
    public static SessionUser require(HttpSession session) {
        SessionUser user = get(session);
        if (user == null) {
            throw new NotLoggedInException();
        }
        return user;
    }

    /** Stores the user in the session (called once, right after a successful login). */
    public static void store(HttpSession session, SessionUser user) {
        session.setAttribute(SESSION_KEY, user);
    }
}
