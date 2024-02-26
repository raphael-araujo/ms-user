package com.raphael.msuser.exception;

public class UniqueCredentialsViolationException extends RuntimeException {

    public UniqueCredentialsViolationException(String message) {
        super(message);
    }
}
