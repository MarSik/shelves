package org.marsik.elshelves.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

@ControllerAdvice
public class ExceptionHandlingController {
	@ExceptionHandler({IOException.class, GeneralSecurityException.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleIoException() {

	}

	@ExceptionHandler(FileNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleFileNotFound() {

	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public void handleBadValues() {

	}
}
