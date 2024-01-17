package com.starlingbank.exceptions;

/**
 * This is a custom exception class for handling service errors.
 * It extends the RuntimeException class and provides additional details about the service error.
 */
public class ServiceException extends RuntimeException {

    /**
     * Default constructor for the ServiceException class.
     */
    public ServiceException() {
        super();
    }

    /**
     * Constructor for the ServiceException class with a custom message.
     * @param message The custom message for the service error.
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constructor for the ServiceException class with a custom message and a cause.
     * @param message The custom message for the service error.
     * @param cause The cause of the service error.
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for the ServiceException class with a cause.
     * @param cause The cause of the service error.
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor for the ServiceException class with a custom message, a cause, and flags for suppression and stack trace.
     * @param message The custom message for the service error.
     * @param cause The cause of the service error.
     * @param enableSuppression Whether or not suppression is enabled or disabled.
     * @param writableStackTrace Whether or not the stack trace should be writable.
     */
    protected ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}