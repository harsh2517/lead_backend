package com.accountooze.exception;


public class UserAuthorizationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	String error;

	public UserAuthorizationException(String error) {
		System.err.println("UserAuthorizationException : "+"cunstructor called"+error);
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
