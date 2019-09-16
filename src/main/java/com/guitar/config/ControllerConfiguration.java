package com.guitar.config;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerConfiguration {
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid data sent to the endpoint")
	public void notValidBadRequest() {
		// possiamo inviare dei messaggi/email a chi di compotenze, quando un erore viene sollevato		
	}
	
	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Data is not valid")
	public void notValidInternalServerError() {
		// possiamo inviare dei messaggi/email a chi di compotenze, quando un erore viene sollevato		
	}
}
