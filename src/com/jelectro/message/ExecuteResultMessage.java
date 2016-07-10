package com.jelectro.message;

import java.io.IOException;

import com.jelectro.processor.MessageProcessorManager;

public class ExecuteResultMessage extends Message {

	private static final long serialVersionUID = 2532683891018414775L;

	private final Message originalExecuteMessage;

	private final Throwable error;
	private transient Object result;
	private final byte[] serializedResult;

	public ExecuteResultMessage(Message executeMessage, Object result, Throwable error) throws IOException {
		super(executeMessage.getMessageId());
		this.originalExecuteMessage = executeMessage;
		this.result = result;
		this.serializedResult = serialize(result);
		this.error = error;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message> void process(MessageProcessorManager messageProcessor, MessageTransporter<M> transporter) throws InterruptedException {
		messageProcessor.getExecuteMessageProcessor().processMessage(this, (MessageTransporter<ExecuteResultMessage>)transporter);

	}

	public Message getOriginalExecuteMessage() {
		return originalExecuteMessage;
	}

	public boolean onError() {
		return error != null;
	}

	public Throwable getError() {
		return error;
	}

	public Object getResult() throws IOException, ClassNotFoundException {
		if (result == null) {
			synchronized(this) {
				if (result == null) 
					result = unserialize(serializedResult);
			}
		}
		return result;
	}

}
