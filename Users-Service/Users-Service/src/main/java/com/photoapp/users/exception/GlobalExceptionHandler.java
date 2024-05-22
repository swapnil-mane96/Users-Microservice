package com.photoapp.users.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ExceptionalMessage> userNotFoundException(UserNotFoundException userNotFoundException){
		String errMsg = userNotFoundException.getMessage();
		return new ResponseEntity<ExceptionalMessage>(new ExceptionalMessage(errMsg, LocalDateTime.now(), false),
				HttpStatus.NOT_FOUND);
	}
}
