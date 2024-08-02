package com.lucasgalmeida.llama.domain.exceptions.file;

public class FileStorageException extends RuntimeException {

    public FileStorageException() {
        super("Failed to store file");
    }
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
