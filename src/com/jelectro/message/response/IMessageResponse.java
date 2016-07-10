package com.jelectro.message.response;

import com.jelectro.exception.TimeoutException;
import com.jelectro.message.Message;

public interface IMessageResponse<M extends Message> {

	/**
	 * Returns the message id associated with this {@link IMessageResponse}.
	 * 
	 * @return
	 */
	public abstract long getMessageId();

	/**
	 * Returns the current available response or null if none.
	 * 
	 * @return
	 */
	public abstract Response<M> peekResponse();

	/**
	 * Wait for a response to be available and returns it. Afterward the
	 * response is removed from this instance.
	 * 
	 * @return the response instance 
	 * @throws InterruptedException
	 * @throws TimeoutException 
	 */
	public abstract Response<M> poolResponse(long timeout) throws InterruptedException, TimeoutException;

	public abstract void addMessage(M message) throws InterruptedException;

	public abstract void addError(Throwable t) throws InterruptedException;

	public abstract void addResponse(M message, Throwable t) throws InterruptedException;

}