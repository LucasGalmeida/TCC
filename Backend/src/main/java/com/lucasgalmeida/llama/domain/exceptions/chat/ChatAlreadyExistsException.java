package com.lucasgalmeida.llama.domain.exceptions.chat;

public class ChatAlreadyExistsException extends RuntimeException {

    public ChatAlreadyExistsException() {
        super("Chat already exists");
    }
    public ChatAlreadyExistsException(String message) {
        super(message);
    }
    public ChatAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
