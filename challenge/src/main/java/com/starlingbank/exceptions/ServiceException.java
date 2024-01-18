package com.starlingbank.exceptions;

/**
 * This is a custom exception class for handling service errors.
 * It extends the RuntimeException class and provides additional details about the service error.
 * @author Cesar Goncalves
 */
public class ServiceException extends RuntimeException {

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

}
