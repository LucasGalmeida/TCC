package com.lucasgalmeida.llama.domain.exceptions.chat;

public class ChatNotFoundException extends RuntimeException {

    public ChatNotFoundException() {
        super("Chat não encontrado");
    }
    public ChatNotFoundException(String message) {
        super(message);
    }
    public ChatNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
