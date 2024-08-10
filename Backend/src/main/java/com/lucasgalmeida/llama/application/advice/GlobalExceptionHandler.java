package com.lucasgalmeida.llama.application.advice;

import com.lucasgalmeida.llama.domain.exceptions.auth.InvalidCredentialsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserAlreadyExistsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserNotFoundException;
import com.lucasgalmeida.llama.domain.exceptions.chat.ChatNotFoundException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentNotFoundException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentStorageException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentTypeException;
import com.lucasgalmeida.llama.domain.exceptions.global.CustomBadRequestException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    // GLOBAL
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<?> handleCustomBadRequestException(CustomBadRequestException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return new ResponseEntity<>("An error occurred while processing the input/output operation.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Map<String, String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Method Not Allowed");
        errors.put("message", ex.getMessage());
        errors.put("supportedMethods", String.join(", ", ex.getSupportedHttpMethods().toString()));
        return new ResponseEntity<>(errors, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Bad Request");
        errors.put("parameter", ex.getParameterName());
        errors.put("message", "O parâmetro '" + ex.getParameterName() + "' é obrigatório e não foi preenchido.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<String> handleDocumentAlreadyExistsException(FileAlreadyExistsException ex) {
        return new ResponseEntity<>("O documento " + ex.getMessage() + " já foi inserido.", HttpStatus.CONFLICT);
    }


    // Chat
    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<String> handleChatNotFoundException(ChatNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}