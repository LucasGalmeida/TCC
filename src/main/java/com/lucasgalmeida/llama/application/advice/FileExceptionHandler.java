package com.lucasgalmeida.llama.application.advice;

import com.lucasgalmeida.llama.domain.exceptions.auth.UserNotFoundException;
import com.lucasgalmeida.llama.domain.exceptions.file.FileStorageException;
import com.lucasgalmeida.llama.domain.exceptions.file.FileTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;

@ControllerAdvice
public class FileExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(FileNotFoundException.class)
    private ResponseEntity<String> fileNotFoundHandler(FileNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(FileStorageException.class)
    private ResponseEntity<String> fileStorageHandler(FileStorageException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(FileTypeException.class)
    private ResponseEntity<String> fileTypeHandler(FileTypeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exception.getMessage());
    }
}