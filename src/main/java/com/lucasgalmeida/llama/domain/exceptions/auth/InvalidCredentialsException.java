package com.lucasgalmeida.llama.domain.exceptions.auth;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid credentials for this user");
    }
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
