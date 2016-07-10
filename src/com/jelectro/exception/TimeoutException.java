package com.jelectro.exception;

public class TimeoutException extends JElectroException{

	
	public TimeoutException(String message) {
		super(message);		
	}
	
	public TimeoutException(String message, Throwable t) {
		super(message, t);		
	}
	

	private static final long serialVersionUID = 1344440232868108189L;

}
