package com.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ EntityNotFoundException.class })
	public ResponseEntity<String> handleNotFound(Exception ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneral(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
	}
}
