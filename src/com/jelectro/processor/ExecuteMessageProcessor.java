package com.jelectro.processor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.jelectro.exception.JElectroException;
import com.jelectro.handler.StubCallbackArgument;
import com.jelectro.message.ExecuteMessage;
import com.jelectro.message.ExecuteResultMessage;
import com.jelectro.message.MessageTransporter;
import com.jelectro.message.response.IMessageResponse;
import com.jelectro.message.response.MessageResponseSingle;
import com.jelectro.node.Node;

public class ExecuteMessageProcessor {

	private static final Logger log = Logger.getLogger(ExecuteMessageProcessor.class);

	private final Node node;

	public ExecuteMessageProcessor(Node node) {
		this.node = node;
	}

	/**
	 * This method process an execute message. It will look for the stub
	 * instance, will find and execute the method with the arguments and will
	 * send back the result to it's original call.
	 * 
	 * @param message
	 * @param transporter
	 * @throws IOException 
	 */
	public void processMessage(ExecuteMessage message, MessageTransporter<ExecuteMessage> transporter) throws IOException {
		
		log.debug("Execute message to proceed");
		
		Object stub = node.getStubContainer().getStub(message.getStubName());
		Throwable error = null;
		Object result = null;
		try {
			if (stub == null)
				throw new JElectroException(String.format("Stub %1$s does not exists", message.getStubName()));

			final Method method = stub.getClass().getDeclaredMethod(message.getMethodName(), message.getParameterTypes());
			method.setAccessible(true);
			Object[] realArguments = processNetworkCallbackArgument(transporter, message.getArguments(), method.getParameterTypes());
			result = method.invoke(stub, realArguments);

		} catch (InvocationTargetException ite) {
			error = ite.getCause() != null ? ite.getCause() : ite;
		} catch (Throwable t) {
			error = t;
		}

		ExecuteResultMessage resultExecuteMessage = new ExecuteResultMessage(message, result, error);
		node.sendMessageSafe(resultExecuteMessage, transporter.getNodePath().reverse());
	}

	protected Object[] processNetworkCallbackArgument(MessageTransporter<ExecuteMessage> transporter, Object[] arguments,
			Class<?>[] parameterTypes) {
		if (arguments == null)
			return null;

		final Object[] realArgs = new Object[arguments.length];

		for (int i = 0; i < arguments.length; i++) {
			Object arg = arguments[i];

			if (arg instanceof StubCallbackArgument) {
				realArgs[i] = ((StubCallbackArgument) arg).getProxy(parameterTypes[i], node, transporter.getNodePath().reverse());
			} else {
				realArgs[i] = arg;
			}
		}
		return realArgs;
	}

	
	/**
	 * This method receives the result of a remote execution. Gets the {@link MessageResponseSingle} waiting for the result and sets the result into it.
	 * @param message
	 * @param transporter
	 * @throws InterruptedException 
	 */
	public void processMessage(ExecuteResultMessage message, MessageTransporter<ExecuteResultMessage> transporter) throws InterruptedException {

		IMessageResponse<ExecuteResultMessage> response = node.getMessageResponseContainer().getMessageResponse(message);
		if (response != null) response.addMessage(message);
		else log.info("Result of an execution is lost : " + message ); 
		
	}

}
