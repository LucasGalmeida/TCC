package com.lucasgalmeida.llama.domain.exceptions.global;

public class CustomBadRequestException extends RuntimeException {
    public CustomBadRequestException() {
        super("Bad request");
    }
    public CustomBadRequestException(String message) {
        super(message);
    }
    public CustomBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

