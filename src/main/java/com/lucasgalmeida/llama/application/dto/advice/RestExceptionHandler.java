package com.lucasgalmeida.llama.application.dto.advice;

import com.lucasgalmeida.llama.domain.exceptions.auth.InvalidCredentialsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserAlreadyExistsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<String> userNotFoundHandler(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<String> userAlreadyExistsHandler(UserAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    private ResponseEntity<String> invalidCredentialsHandler(InvalidCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }
}
