package com.campusbite.common;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * SHARED FILE (owner: Lawk).
 * @ControllerAdvice = these @ExceptionHandler methods apply to EVERY controller
 * in the app. If a controller (or the service it calls) throws one of these
 * exceptions, Spring calls the matching method here instead of showing a stack trace.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** A record was not found: show templates/error/404.html with HTTP status 404. */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    /** A URL like /menu/items/abc where a number was expected: also a 404. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBadId(Model model) {
        model.addAttribute("message", "That page does not exist.");
        return "error/404";
    }

    /** The page needs a logged-in user: send the browser to the login page. */
    @ExceptionHandler(NotLoggedInException.class)
    public String handleNotLoggedIn() {
        return "redirect:/login";
    }
}
