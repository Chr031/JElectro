package com.jelectro.stubs;

import org.apache.log4j.Logger;

import com.jelectro.message.LookupResultMessage;
import com.jelectro.message.response.IResponseListener;
import com.jelectro.message.response.MessageResponseMulti;
import com.jelectro.message.response.Response;
import com.jelectro.node.Node;
import com.jelectro.utils.WeakFireListeners;

public class LookupResultStubProducer<S> implements IElementProducer<StubReference<S>>, IResponseListener<LookupResultMessage> {

	private static final Logger log = Logger.getLogger(LookupResultStubProducer.class);

	private final WeakFireListeners<IElementProducerListener<StubReference<S>>> listeners;
	private final MessageResponseMulti<LookupResultMessage> messageResponse;
	private final Node node;
	private final Class<S> stubInterface;

	public LookupResultStubProducer(Node node, Class<S> stubInterface, MessageResponseMulti<LookupResultMessage> messageResponse) {
		this.listeners = new WeakFireListeners<IElementProducerListener<StubReference<S>>>();

		this.node = node;
		this.stubInterface = stubInterface;
		this.messageResponse = messageResponse;

		messageResponse.addResponseListener(this);
	}

	public MessageResponseMulti<LookupResultMessage> getMessageResponse() {
		return messageResponse;
	}

	@Override
	public void addElementProducerListener(IElementProducerListener<StubReference<S>> spl) {
		listeners.addListener(spl);
		log.debug("ElementProducer created");
	}

	@Override
	public void onResponseReceived(Response<LookupResultMessage> response) {

		if (response.getMessage() != null) {
			LookupResultMessage message = response.getMessage();
			for (String stubName : message.getMatchingStubNames()) {
				StubReference<S> sRef = new StubReference<S>(node, stubName, stubInterface, message.getStubNodePath());
				addElement(sRef);
				log.debug("New StubReference added");
			}
		}
	}

	@Override
	public void addElement(StubReference<S> ref) {
		if (listeners.isFireProxyReady())
			listeners.getFireProxy().onElementProduced(ref);
		
	}

}
