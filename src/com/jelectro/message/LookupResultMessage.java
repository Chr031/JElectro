package com.jelectro.message;

import com.jelectro.node.NodeKey;
import com.jelectro.node.NodePath;
import com.jelectro.processor.MessageProcessorManager;

public class LookupResultMessage extends Message {

	private static final long serialVersionUID = -8217943237366367798L;
	private final String[] matchingStubNames;
	private final NodeKey instanceNodeKey;
	private NodePath stubNodePath;

	public LookupResultMessage(LookupMessage originalMessage, String[] matchingStubNames, NodeKey instanceNodeKey) {
		super(originalMessage.getMessageId());
		this.matchingStubNames = matchingStubNames;
		this.instanceNodeKey = instanceNodeKey;
	}

	@Override
	public <M extends Message> void process(MessageProcessorManager messageProcessor, MessageTransporter<M> transporter)
			throws InterruptedException {
		messageProcessor.getLookupMessageProcessor().processMessage(this, (MessageTransporter<LookupResultMessage>) transporter);

	}

	public String[] getMatchingStubNames() {
		return matchingStubNames;
	}

	public NodeKey getInstanceNodeKey() {
		return instanceNodeKey;
	}

	public NodePath getStubNodePath() {
		return stubNodePath;
	}

	public void setStubNodePath(NodePath nodePath) {
		this.stubNodePath = nodePath;
	}

}
