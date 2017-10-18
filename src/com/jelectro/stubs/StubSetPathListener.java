package com.jelectro.stubs;

public interface StubSetPathListener<S> extends StubSetListener<S> {
	
	/**
	 * <p>
	 * This method is called when a stub that was already discovered is added
	 * again. Instead of been added, the new path is added.
	 * </p>
	 * <p>
	 * <b>This method is not accurate, in the sense that it may not be called
	 * for all possible path due to the message propagation method.</b> This is
	 * due to the non propagation behavior that may avoid new path to be
	 * discovered according to the order the lookup messages are processed and
	 * propagated through the nodes.
	 * </p>
	 * 
	 * @param stub
	 */
	void onStubPathUpdated(S stub);
}
