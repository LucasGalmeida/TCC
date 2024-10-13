package com.lucasgalmeida.llama.domain.exceptions.document;

public class DocumentTypeException extends RuntimeException {
    public DocumentTypeException() {
        super("Tipo de arquivo não suportado");
    }
    public DocumentTypeException(String message) {
        super(message);
    }
    public DocumentTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}