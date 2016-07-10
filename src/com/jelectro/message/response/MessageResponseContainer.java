package com.jelectro.message.response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jelectro.message.Message;

public class MessageResponseContainer {
	private final Map<Long, IMessageResponse<?>> messageResponseMap;

	public MessageResponseContainer() {
		messageResponseMap = new ConcurrentHashMap<Long, IMessageResponse<?>>();
	}

	public <M extends Message> IMessageResponse<M> createMessageResponseSingle(M message) {
		MessageResponseSingle<M> response = new MessageResponseSingle<M>(message.getMessageId());
		messageResponseMap.put(message.getMessageId(), response);
		return response;
	}

	public <M extends Message> void registerResponse(IMessageResponse<M> response) {
		messageResponseMap.put(response.getMessageId(), response);
	}
	
	@SuppressWarnings("unchecked")
	public <M extends Message > IMessageResponse<M> getMessageResponse(M message) {
		return (IMessageResponse<M>) messageResponseMap.get(message.getMessageId());
	}
	
}
