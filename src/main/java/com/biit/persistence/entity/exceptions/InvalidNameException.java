package com.biit.persistence.entity.exceptions;

public class InvalidNameException extends Exception {
    private static final long serialVersionUID = 8192681412925790911L;

    public InvalidNameException(String message) {
        super(message);
    }
}
