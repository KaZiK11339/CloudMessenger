package com.cloudmessenger.exception;

/**
 * Исключение для обработки ошибок при работе с файлами
 */
public class FileStorageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
} 