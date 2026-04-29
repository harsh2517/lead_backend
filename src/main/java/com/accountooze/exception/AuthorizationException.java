package com.accountooze.exception;


public class AuthorizationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	String error;

	public AuthorizationException(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
