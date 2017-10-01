package com.jelectro;

public interface ConnectionListener {

	public enum ConnectionEvent { CONNECTION, DISCONNECTION }
	
	public void onConnectionEvent(ConnectionEvent event) ;
	
	
	
}
