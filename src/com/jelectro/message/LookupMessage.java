package com.jelectro.message;

import com.jelectro.processor.MessageProcessorManager;

public class LookupMessage extends Message {

	private static final long serialVersionUID = -2621762605334097102L;

	private final String stubNameRegEx;
	private final Class<?> stubInterface;

	public LookupMessage(String stubNameRegEx, Class<?> stubInterface) {
		super();
		this.stubNameRegEx = stubNameRegEx;
		this.stubInterface = stubInterface;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message> void process(MessageProcessorManager messageProcessor, MessageTransporter<M> transporter) {
		messageProcessor.getLookupMessageProcessor().processMessage(this, (MessageTransporter<LookupMessage>) transporter);

	}


	public String getStubNameRegEx() {
		return stubNameRegEx;
	}

	public Class<?> getStubInterface() {
		return stubInterface;
	}

	

}
