package com.lucasgalmeida.llama.domain.exceptions.auth;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Você não tem permissão");
    }
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
