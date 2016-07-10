package com.jelectro.exception;

public class PathNotAvailableException extends JElectroException {

	
	private static final long serialVersionUID = -1068176227015295058L;

	public PathNotAvailableException(String message) {
		super(message);
		
	}

	public PathNotAvailableException(String message, Throwable t) {
		super(message, t);
	}

}
