package com.jelectro.message;

import com.jelectro.processor.MessageProcessorManager;

public class ErrorMessage<M extends Message> extends Message {

	private static final long serialVersionUID = -4655836152751326824L;

	private final Throwable error;
	private final M originalMessage;

	public ErrorMessage(M message, Throwable error) {
		super(message.getMessageId());
		this.originalMessage = message;
		this.error = error;
	}

	@Override
	public <M2 extends Message> void process(MessageProcessorManager messageProcessor, MessageTransporter<M2> transporter)
			throws InterruptedException {
		messageProcessor.getErrorMessageProcessor().processMessage(this, (MessageTransporter<ErrorMessage<M>>) transporter);

	}

	public Throwable getError() {
		return error;
	}

	public M getOriginalMessage() {
		return originalMessage;
	}

}
