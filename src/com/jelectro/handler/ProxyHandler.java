package com.jelectro.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jelectro.JElectro;
import com.jelectro.message.ExecuteMessage;
import com.jelectro.message.ExecuteResultMessage;
import com.jelectro.message.Message;
import com.jelectro.message.response.MessageResponseSingle;
import com.jelectro.message.response.Response;
import com.jelectro.node.Node;
import com.jelectro.node.NodePathList;
import com.jelectro.stubs.IStub;

public class ProxyHandler implements InvocationHandler, IStub {

	protected final String stubName;

	protected final Node node;
	protected final NodePathList nodePaths;

	/**
	 * TODO should be instantiated with the good node path. No need to reverse
	 * for each call.
	 * 
	 * @param stubName
	 * @param node
	 * @param stubNodePaths
	 */
	public ProxyHandler(String stubName, Node node, NodePathList stubNodePaths) {

		this.stubName = stubName;
		this.node = node;
		this.nodePaths = stubNodePaths;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		// test if the call is pointing to an IStub method !
		if (method.getDeclaringClass().equals(IStub.class)) {
			return method.invoke(this, args);
		}
		// DONE Implement there a fail over when path are not found !
		
		final Message message = new ExecuteMessage(stubName, method, args);
		final MessageResponseSingle<ExecuteResultMessage> messageResponse = new MessageResponseSingle<ExecuteResultMessage>(message.getMessageId());
		node.sendMessageRegisterResponse(message, messageResponse, nodePaths.getMainPath());

		// TODO implements a timeout. This will be necessary to avoid hanging ...
		final Response<ExecuteResultMessage> response = messageResponse.poolResponse(getTimeout());
		if (response.getError() != null)
			throw response.getError();

		if (response.getMessage().getError() != null)
			throw response.getMessage().getError();

		final Object result = response.getMessage().getResult();

		return result;
	}

	
	/// IStub methods 
	
	private long timeout = JElectro.DEFAULT_STUB_TIMEOUT;
	
	@Override
	public long getTimeout() {
		return timeout;
	}

	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}
