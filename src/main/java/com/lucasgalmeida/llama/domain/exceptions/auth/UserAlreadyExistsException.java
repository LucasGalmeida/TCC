package com.lucasgalmeida.llama.domain.exceptions.auth;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User already registred");
    }
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
