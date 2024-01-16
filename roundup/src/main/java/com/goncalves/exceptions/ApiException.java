package com.goncalves.exceptions;

/**
 * Custom exception class for API errors.
 */
public class ApiException extends Exception {
    private final int statusCode;
    private final String error;
    private final String errorDescription;

    /**
     * Constructs a new ApiException with the specified status code, error, and error description.
     *
     * @param statusCode       the HTTP status code of the API error
     * @param error            the error code or type of the API error
     * @param errorDescription a description of the API error
     */
    public ApiException(int statusCode, String error, String errorDescription) {
        super(String.format("Status Code: %s, Error - Status: %s, Description: %s", statusCode, error, errorDescription));
        this.statusCode = statusCode;
        this.error = error;
        this.errorDescription = errorDescription;
    }

    /**
     * Returns the HTTP status code of the API error.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the error code or type of the API error.
     *
     * @return the error code or type
     */
    public String getError() {
        return error;
    }

    /**
     * Returns the description of the API error.
     *
     * @return the error description
     */
    public String getErrorDescription() {
        return errorDescription;
    }
}