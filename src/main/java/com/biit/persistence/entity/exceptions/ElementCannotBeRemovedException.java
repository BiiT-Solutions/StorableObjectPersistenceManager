package com.biit.persistence.entity.exceptions;

public class ElementCannotBeRemovedException extends Exception {
    private static final long serialVersionUID = 8467077148811245237L;

    public ElementCannotBeRemovedException(String message) {
        super(message);
    }
}
