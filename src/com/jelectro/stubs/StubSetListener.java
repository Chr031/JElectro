package com.jelectro.stubs;

public interface StubSetListener<S> {
	/**
	 * This method is called when a new stub is discovered.
	 * 
	 * @param stub
	 */
	void onStubReceived(S stub);
	
}