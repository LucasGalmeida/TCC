package com.lucasgalmeida.llama.domain.exceptions.document;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException() {
        super("Document not found");
    }
    public DocumentNotFoundException(String message) {
        super(message);
    }
    public DocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
