package com.jelectro.processor;

import com.jelectro.message.MessageTransporter;

public class MessageProcessorManager {

	
	private ExecuteMessageProcessor executeMessageProcessor;
	private ErrorMessageProcessor errorMessageProcessor;
	private LookupMessageProcessor lookupMessageProcessor;

	public MessageProcessorManager() {

	}

	/**
	 * 
	 * @param transporter
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void processMessage(MessageTransporter transporter) throws Throwable {
		transporter.getMessage().process(this, transporter);

	}

	public ExecuteMessageProcessor getExecuteMessageProcessor() {
		return executeMessageProcessor;
	}

	public void setExecuteMessageProcessor(ExecuteMessageProcessor executeMessageProcessor) {
		this.executeMessageProcessor = executeMessageProcessor;
	}

	public ErrorMessageProcessor getErrorMessageProcessor() {
		return errorMessageProcessor;
	}

	public void setErrorMessageProcessor(ErrorMessageProcessor errorMessageProcessor) {
		this.errorMessageProcessor = errorMessageProcessor;
	}

	public LookupMessageProcessor getLookupMessageProcessor() {
		return lookupMessageProcessor;
	}

	public void setLookupMessageProcessor(LookupMessageProcessor lookupMessageProcessor) {
		this.lookupMessageProcessor = lookupMessageProcessor;
	}

	
	

}
