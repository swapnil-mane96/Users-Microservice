package com.photoapp.users.exception;

public class UserNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4154055009606663618L;
	
	public UserNotFoundException(String message) {
		super(message);
	}

}
