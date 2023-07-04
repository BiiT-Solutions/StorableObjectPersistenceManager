package com.biit.persistence.entity.exceptions;

public class EntityIsNullException extends Exception {
    private static final long serialVersionUID = -985274828877387118L;

    public EntityIsNullException(String message) {
        super(message);
    }
}
