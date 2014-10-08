package com.biit.persistence.entity.exceptions;

public class NotValidStorableObjectException extends Exception {
	private static final long serialVersionUID = 6836625136344196750L;

	public NotValidStorableObjectException(String message) {
		super(message);
	}
}
