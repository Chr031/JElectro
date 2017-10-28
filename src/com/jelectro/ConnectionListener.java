package com.jelectro;

import com.jelectro.node.NodeKey;

public interface ConnectionListener {

	public enum EventType { CONNECTION, DISCONNECTION }
	
	public class ConnectionEvent {
		
		private final EventType type;
		private final NodeKey remoteNodeKey;
		private final String remoteHost;
		private final int remotePort;
		
		public ConnectionEvent(EventType type, NodeKey remoteNodeKey, String remoteHost, int remotePort) {
			super();
			this.type = type;
			this.remoteNodeKey = remoteNodeKey;
			this.remoteHost = remoteHost;
			this.remotePort = remotePort;
		}
		
		
	}
	
	public void onConnectionEvent(ConnectionEvent event) ;	
	
}


