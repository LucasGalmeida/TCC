package com.lucasgalmeida.llama.domain.exceptions.file;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException() {
        super("File not found");
    }
    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
