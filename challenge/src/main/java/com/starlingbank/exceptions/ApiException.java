package com.starlingbank.exceptions;

/**
 * This is a custom exception class for handling API errors.
 * It extends the Exception class and provides additional details about the API error.
 * @author Cesar Goncalves
 */
public class ApiException extends Exception {
    // Status code of the API response
    private final int statusCode;
    // Error message of the API response
    private final String error;
    // Detailed description of the API error
    private final String errorDescription;

    /**
     * Constructor for the ApiException class.
     * It initializes the status code, error message, and error description of the API error.
     *
     * @param statusCode       The status code of the API response.
     * @param error            The error message of the API response.
     * @param errorDescription The detailed description of the API error.
     */
    public ApiException(int statusCode, String error, String errorDescription) {
        super(String.format("Status Code: %d, Error: %s, Description: %s", statusCode, error, errorDescription));
        this.statusCode = statusCode;
        this.error = error;
        this.errorDescription = errorDescription;
    }

    /**
     * This method overrides the getMessage method of the Exception class.
     * It provides a custom message format for the API error.
     *
     * @return A string representing the custom message format of the API error.
     */
    @Override
    public String getMessage() {
        return String.format("HTTP %d - %s: %s", statusCode, error, errorDescription);
    }

}
