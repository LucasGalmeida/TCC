package com.lucasgalmeida.llama.domain.exceptions.auth;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciais inválidas para este usuário");
    }
    public InvalidCredentialsException(String message) {
        super(message);
    }
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
