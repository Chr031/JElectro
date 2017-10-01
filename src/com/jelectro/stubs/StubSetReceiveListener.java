package com.jelectro.stubs;

import java.util.function.Consumer;

public class StubSetReceiveListener<St> implements StubSetListener<St>{

	private final Consumer<St> receiveConsumer;
	
	public StubSetReceiveListener(Consumer<St> receiveConsumer) {
		super();
		this.receiveConsumer = receiveConsumer;
	}

	@Override
	public void onStubReceived(St stub) {		
		receiveConsumer.accept(stub);
	}

	@Override
	public void onStubPathUpdated(St stub) {
		// no opps		
	}

}
