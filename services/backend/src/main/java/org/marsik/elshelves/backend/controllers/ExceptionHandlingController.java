package org.marsik.elshelves.backend.controllers;

import org.marsik.elshelves.backend.controllers.exceptions.UserExists;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ExceptionHandlingController {
    @ResponseStatus(value= HttpStatus.CONFLICT, reason="User already exists")
    @ExceptionHandler(UserExists.class)
    public void conflict() {
        // Nothing to do
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN, reason="Permission denied")
    @ExceptionHandler(UserExists.class)
    public void denied() {
        // Nothing to do
    }
}
