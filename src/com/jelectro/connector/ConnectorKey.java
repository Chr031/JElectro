package com.jelectro.connector;

import com.jelectro.node.NodeKey;

public class ConnectorKey {

	private final NodeKey remoteNodeKey;
	private final NodeKey localNodeKey;

	public ConnectorKey(NodeKey localNodeKey, NodeKey remoteNodeKey) {
		super();
		this.localNodeKey = localNodeKey;
		this.remoteNodeKey = remoteNodeKey;
	}

	public NodeKey getLocalNodeKey() {
		return localNodeKey;
	}

	public NodeKey getRemoteNodeKey() {
		return remoteNodeKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((localNodeKey == null) ? 0 : localNodeKey.hashCode());
		result = prime * result + ((remoteNodeKey == null) ? 0 : remoteNodeKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectorKey other = (ConnectorKey) obj;
		if (remoteNodeKey == null) {
			if (other.remoteNodeKey != null)
				return false;
		} else if (!remoteNodeKey.equals(other.remoteNodeKey))
			return false;
		if (localNodeKey == null) {
			if (other.localNodeKey != null)
				return false;
		} else if (!localNodeKey.equals(other.localNodeKey))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ConnectorKey [remoteNodeKey=" + remoteNodeKey + ", localNodeKey=" + localNodeKey + "]";
	}

}
