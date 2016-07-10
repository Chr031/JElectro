package com.jelectro.message;

import java.io.Serializable;

import com.jelectro.node.NodeKey;
import com.jelectro.node.NodePath;

public class MessageTransporter<M extends Message> implements Serializable {

	private static final long serialVersionUID = -7340687672774556375L;

	private final NodePath nodePath;
	private final M message;

	public MessageTransporter(NodePath nodePath, M message) {
		this.nodePath = nodePath;
		this.message = message;
	}

	public M getMessage() {
		return message;
	}

	public NodePath getNodePath() {
		return nodePath;
	}

	public boolean isDestination(NodeKey nodeKey) {
		return nodePath.getDestinationNode().equals(nodeKey);
	}

	public NodeKey getNextNode(NodeKey nodeKey) {
		return nodePath.getNextNode(nodeKey);
	}

	public NodeKey getPreviousNode(NodeKey nodeKey) {
		return nodePath.getPreviousNode(nodeKey);
	}

	public NodeKey getDestination() {
		return nodePath.getDestinationNode();
	}

	@Override
	public String toString() {
		return "MessageTransporter [nodePath=" + nodePath + ", message=" + message + "]";
	}

}
