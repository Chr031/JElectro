package com.jelectro.message.response;

import com.jelectro.exception.TimeoutException;
import com.jelectro.message.Message;

public class MessageResponseSingle<M extends Message> implements IMessageResponse<M> {

	private final Object lock;

	private final long messageId;

	private Response<M> response;

	public MessageResponseSingle(long messageId) {
		this.lock = new Object();
		this.messageId = messageId;
	}

	/* (non-Javadoc)
	 * @see com.jelectro.message.MessageResponseI#getMessageId()
	 */
	@Override
	public long getMessageId() {
		return messageId;
	}

	
	@Override
	public Response<M> peekResponse() {
		return response;
	}

	@Override
	public Response<M> poolResponse(long timeout) throws InterruptedException, TimeoutException {
		long start = timeout ==0 ? Long.MAX_VALUE : System.currentTimeMillis();
		if (response == null) {
			try {
				synchronized (lock) {
					while (response == null &&  start > System.currentTimeMillis() - timeout ) {
						lock.wait(timeout);
					}
				}
			} catch (InterruptedException ie) {
				throw ie;
			}
			if (response == null && start <= System.currentTimeMillis() - timeout )
				throw new TimeoutException("Timeout of " + timeout + " ms reached");
		}		
		return response;
	}

	@Override
	public void addMessage(M message) {
		addResponse(message, null);
		
	}

	@Override
	public void addError(Throwable t) {
		addResponse(null, t);
		
	}

	@Override
	public void addResponse(M message, Throwable t) {
		response = new Response<M> (message, t);
		synchronized (lock) {
			lock.notifyAll();
		}
	}

}
