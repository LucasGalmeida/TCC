package com.lucasgalmeida.llama.domain.exceptions.document;

public class DocumentStorageException extends RuntimeException {

    public DocumentStorageException() {
        super("Falha ao salvar arquivo");
    }
    public DocumentStorageException(String message) {
        super(message);
    }

    public DocumentStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
