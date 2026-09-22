package com.projtechhub.techhub.exceptions;

/**
 * @author pc
 **/
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
