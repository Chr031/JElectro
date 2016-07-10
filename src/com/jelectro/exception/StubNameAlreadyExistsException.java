package com.jelectro.exception;

public class StubNameAlreadyExistsException extends JElectroException {

	
	private static final long serialVersionUID = 6765650216770614549L;

	public StubNameAlreadyExistsException(String stubName) {
		super(String.format("this name '%s' is already used", stubName));
		
	}

}
