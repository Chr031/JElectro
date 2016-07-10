package com.jelectro.exception;

/**
 * 
 * TODO Implement a multiple cause exception class.
 * 
 * @author xneb
 */
public class JElectroException extends Exception {

	private static final long serialVersionUID = 1L;

	public JElectroException(String message) {
		super(message);
	}

	public JElectroException(String message, Throwable t) {
		super(message, t);
	}

}
