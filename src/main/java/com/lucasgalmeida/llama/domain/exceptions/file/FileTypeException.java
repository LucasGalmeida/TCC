package com.lucasgalmeida.llama.domain.exceptions.file;

public class FileTypeException extends RuntimeException {
    public FileTypeException() {
        super("Unsupported file type");
    }
    public FileTypeException(String message) {
        super(message);
    }
    public FileTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}