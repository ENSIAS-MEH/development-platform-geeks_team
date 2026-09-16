package com.projtechhub.techhub.exceptions;

/**
 * @author pc
 **/
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
