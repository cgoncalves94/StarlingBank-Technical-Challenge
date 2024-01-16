package com.goncalves.exceptions;

public class ApiException extends Exception {
    private final int statusCode;
    private final String error;
    private final String errorDescription;

    public ApiException(int statusCode, String error, String errorDescription) {
        super(String.format("Status Code: %s, Error - Status: %s, Description: %s", statusCode, error, errorDescription));
        this.statusCode = statusCode;
        this.error = error;
        this.errorDescription = errorDescription;
    }

    // Getters for the status code, error, and description
    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    
}