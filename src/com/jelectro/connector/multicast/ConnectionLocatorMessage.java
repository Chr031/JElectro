package com.jelectro.connector.multicast;

import java.io.Serializable;

import com.jelectro.node.NodeKey;

public class ConnectionLocatorMessage implements Serializable {

	private static final long serialVersionUID = -935388692068611166L;

	private final NodeKey senderNodeKey;

	private final ConnectionInfo connectionInfo;

	public ConnectionLocatorMessage(NodeKey senderNodeKey, ConnectionInfo connectionInfo) {
		super();
		this.senderNodeKey = senderNodeKey;
		this.connectionInfo = connectionInfo;
	}

	public NodeKey getSenderNodeKey() {
		return senderNodeKey;
	}

	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

}
