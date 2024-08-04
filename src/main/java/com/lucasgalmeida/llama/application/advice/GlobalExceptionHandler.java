package com.lucasgalmeida.llama.application.advice;

import com.lucasgalmeida.llama.domain.exceptions.auth.InvalidCredentialsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserAlreadyExistsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserNotFoundException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentNotFoundException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentStorageException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentTypeException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // GLOBAL
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        System.err.println("IOException occurred: " + ex.getMessage());
        return new ResponseEntity<>("An error occurred while processing the input/output operation.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // AUTH
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

    // DOCUMENTS
    @ExceptionHandler(FileNotFoundException.class)
    private ResponseEntity<String> documentNotFoundHandler(FileNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(DocumentStorageException.class)
    private ResponseEntity<String> documentStorageHandler(DocumentStorageException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(DocumentTypeException.class)
    private ResponseEntity<String> documentTypeHandler(DocumentTypeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exception.getMessage());
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<String> handleDocumentNotFoundException(DocumentNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}