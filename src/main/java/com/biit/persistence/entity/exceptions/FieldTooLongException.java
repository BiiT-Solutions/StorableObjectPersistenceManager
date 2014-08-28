package com.biit.persistence.entity.exceptions;

public class FieldTooLongException extends Exception {
	private static final long serialVersionUID = -5943640936441164338L;

	public FieldTooLongException(String message) {
		super(message);
	}
}
