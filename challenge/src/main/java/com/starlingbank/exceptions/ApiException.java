package com.starlingbank.exceptions;

/**
 * Custom exception class for API errors.
 */
public class ApiException extends Exception {
    private final int statusCode;
    private final String error;
    private final String errorDescription;

    public ApiException(int statusCode, String error, String errorDescription) {
        super(String.format("Status Code: %d, Error: %s, Description: %s", statusCode, error, errorDescription));
        this.statusCode = statusCode;
        this.error = error;
        this.errorDescription = errorDescription;
    }

    @Override
    public String getMessage() {
        // Provide a custom message format if needed
        return String.format("HTTP %d - %s: %s", statusCode, error, errorDescription);
    }

}