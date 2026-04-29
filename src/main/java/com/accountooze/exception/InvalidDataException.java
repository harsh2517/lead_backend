package com.accountooze.exception;


public class InvalidDataException extends RuntimeException {


	private static final long serialVersionUID = 1L;
	String error;

	public InvalidDataException(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
