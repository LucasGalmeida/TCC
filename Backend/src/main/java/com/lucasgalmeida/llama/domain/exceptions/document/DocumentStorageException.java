package com.lucasgalmeida.llama.domain.exceptions.document;

public class DocumentStorageException extends RuntimeException {

    public DocumentStorageException() {
        super("Failed to store file");
    }
    public DocumentStorageException(String message) {
        super(message);
    }

    public DocumentStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
