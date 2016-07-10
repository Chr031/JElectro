package com.jelectro.message.response;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.blue.tools.utils.WeakFireListeners;

import com.jelectro.JElectro;
import com.jelectro.message.Message;

import tools.logger.Logger;

public class MessageResponseMulti<M extends Message> implements IMessageResponse<M> {

	private final Logger log = Logger.getLogger(MessageResponseMulti.class);
	
	
	private final long messageId;
	
	private final BlockingQueue<Response<M>> queue;
	private final WeakFireListeners<IResponseListener<M>> listeners;

	public MessageResponseMulti(long messageId) {
		queue = new LinkedBlockingQueue<Response<M>>();
		listeners = new WeakFireListeners<IResponseListener<M>>();
		this.messageId = messageId;
		
	}

	@Override
	public long getMessageId() {
		return messageId;
	}

	@Override
	public Response<M> peekResponse() {
		return queue.peek();
	}

	@Override
	public Response<M> poolResponse(long timeout) throws InterruptedException {
		if (timeout > 0 ) 
			return queue.poll(timeout, TimeUnit.MILLISECONDS);
		else 
			return queue.take();
	}


	@Override
	public void addMessage(M message) throws InterruptedException {
		addResponse(message, null);
		
	}

	@Override
	public void addError(Throwable error) throws InterruptedException {
		addResponse(null, error);
		
	}

	@Override
	public void addResponse(M message, Throwable error) throws InterruptedException {
		final Response<M> response = new Response<M>(message, error);
		queue.put(response);
		if (listeners.isFireProxyReady()) 
			listeners.getFireProxy().onResponseReceived(response);
		log.debug("Response received");
	}

	public void addResponseListener(IResponseListener<M> listener) {
		listeners.addListener(listener);
	}
	

}
