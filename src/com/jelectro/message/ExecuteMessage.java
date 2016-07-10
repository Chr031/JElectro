package com.jelectro.message;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.jelectro.processor.MessageProcessorManager;

public class ExecuteMessage extends Message {

	private static final long serialVersionUID = -2415371921128046963L;

	private final String stubName;
	private final String methodName;
	private final Class<?>[] parameterTypes;
	private transient Object[] arguments;
	private final byte[] serializedArguments;

	public ExecuteMessage(String stubName, Method method, Object[] arguments) throws IOException {
		super();
		this.stubName = stubName;
		this.methodName = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.arguments = arguments;
		this.serializedArguments = serialize(arguments);

	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message> void process(MessageProcessorManager messageProcessor, MessageTransporter<M> transporter) throws IOException {
		messageProcessor.getExecuteMessageProcessor().processMessage(this, (MessageTransporter<ExecuteMessage>) transporter);

	}

	public String getStubName() {
		return stubName;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public Object[] getArguments() throws IOException, ClassNotFoundException {
		if (arguments == null)
			synchronized (this) {
				if (arguments == null)
					arguments = (Object[]) unserialize(serializedArguments);
			}

		return arguments;
	}

	@Override
	public String toString() {
		String toString = "ExecuteMessage [messageId=" + messageId + ", stubName=" + stubName + ", methodName=" + methodName + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + ", arguments=";
		try {
			toString += Arrays.toString(getArguments());
		} catch (Exception e) {
			toString += e.getMessage();
		}
		return toString + "]";

	}

}
