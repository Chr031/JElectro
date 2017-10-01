package com.jelectro.processor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;

import com.jelectro.connector.ConnectorKey;
import com.jelectro.message.LookupMessage;
import com.jelectro.message.LookupResultMessage;
import com.jelectro.message.MessageTransporter;
import com.jelectro.message.response.IMessageResponse;
import com.jelectro.node.Node;
import com.jelectro.node.NodeKey;
import com.jelectro.node.NodePath;

public class LookupMessageProcessor {

	private static final Logger log = Logger.getLogger(LookupMessageProcessor.class);

	private final Node node;

	private final Set<Long> processedLocateMessage;

	public LookupMessageProcessor(Node node) {
		this.node = node;
		processedLocateMessage = new ConcurrentSkipListSet<Long>();
	}

	/**
	 * <ul>
	 * Processes a locate message.
	 * 
	 * <li>Looks if a stub present in this node is not matching the current
	 * {@link LookupMessage#getSearchString()} value. if yes, sends back a
	 * {@link LookupResultMessage}</li>
	 * <li>Check if this locate message has already been treated.</li>
	 * <li>If not, forwards it to all connectors (but not the incoming one).
	 * </li>
	 * </ul>
	 * 
	 * @param lookupMessage
	 * @param transporter
	 */
	public void processMessage(LookupMessage lookupMessage, MessageTransporter<LookupMessage> transporter) {

		// TODO : Check if the regex is valid according to the path....

		log.debug("process new lookup message");

		// 1- look for a local stub
		String[] matchingStubNames = node.getStubContainer().getPublicMatchingStubNames(lookupMessage.getStubNameRegEx(),
				lookupMessage.getStubInterface());
		if (matchingStubNames.length > 0) {
			LookupResultMessage lookupResultMessage = new LookupResultMessage(lookupMessage, matchingStubNames, node.getNodeKey());
			node.sendMessageSafe(lookupResultMessage, transporter.getNodePath().reverse());
		}

		// 2-
		boolean alreadyProcessed = false;
		synchronized (this) {
			alreadyProcessed = processedLocateMessage.contains(lookupMessage.getMessageId());
			if (!alreadyProcessed)
				processedLocateMessage.add(lookupMessage.getMessageId());
		}

		// 3-
		if (!alreadyProcessed) {

			// NodeKey previousNodeKey =
			// transporter.getPreviousNode(node.getNodeKey());
			Set<ConnectorKey> connectorKeys = node.getConnectorContainer().getConnectorKeySet();
			for (NodeKey nk : transporter.getNodePath().getKeysPath()) {
				if (connectorKeys.remove(new ConnectorKey(node.getNodeKey(), nk)))
					log.debug("Path removed : " + nk);
			}

			for (ConnectorKey key : connectorKeys) {
				node.sendMessageSafe(lookupMessage, new NodePath(transporter.getNodePath(), key.getRemoteNodeKey()));
			}
		} else {
			log.debug("Message already processed");
		}
	}

	/**
	 * Get the MessageResponse and add the new incoming result to it !
	 * 
	 * @param locateMessage
	 * @param transporter
	 * @throws InterruptedException
	 */
	public void processMessage(LookupResultMessage lookupResultMessage, MessageTransporter<LookupResultMessage> transporter)
			throws InterruptedException {

		IMessageResponse<LookupResultMessage> response = node.getMessageResponseContainer().getMessageResponse(lookupResultMessage);
		if (response != null) {
			lookupResultMessage.setStubNodePath(transporter.getNodePath().reverse());
			response.addMessage(lookupResultMessage);
		}
	}

}
