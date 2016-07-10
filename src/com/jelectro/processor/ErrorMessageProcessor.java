package com.jelectro.processor;

import com.jelectro.message.ErrorMessage;
import com.jelectro.message.Message;
import com.jelectro.message.MessageTransporter;
import com.jelectro.message.response.IMessageResponse;
import com.jelectro.node.Node;

public class ErrorMessageProcessor {

	private final Node node;

	public ErrorMessageProcessor(Node node) {
		this.node = node;
	}

	/**
	 * <b>Receives error message coming from bad path routing.</b> Mainly if the path
	 * is not valid, such a message is sent back to the sender.
	 * 
	 * @param errorMessage
	 * @param transporter
	 * @throws InterruptedException
	 */
	public <M extends Message> void processMessage(ErrorMessage<M> errorMessage, MessageTransporter<ErrorMessage<M>> transporter)
			throws InterruptedException {
		IMessageResponse<M> response = node.getMessageResponseContainer().getMessageResponse(errorMessage.getOriginalMessage());
		if (response != null)
			response.addError(errorMessage.getError());
	}

}
