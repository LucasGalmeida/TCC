package com.lucasgalmeida.llama.domain.exceptions.document;

public class DocumentTypeException extends RuntimeException {
    public DocumentTypeException() {
        super("Unsupported file type");
    }
    public DocumentTypeException(String message) {
        super(message);
    }
    public DocumentTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}