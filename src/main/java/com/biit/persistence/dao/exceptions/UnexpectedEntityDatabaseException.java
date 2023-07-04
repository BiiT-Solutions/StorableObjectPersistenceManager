package com.biit.persistence.dao.exceptions;

public class UnexpectedEntityDatabaseException extends Exception {
    private static final long serialVersionUID = 8438018391045513212L;

    public UnexpectedEntityDatabaseException(String message, Exception originException) {
        super(message, originException);
    }
}
