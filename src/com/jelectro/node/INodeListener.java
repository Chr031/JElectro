package com.jelectro.node;

import com.jelectro.connector.IConnector;

public interface INodeListener {

	void onConnectorAdded(IConnector connector) ;
	
	void onConnectorRemoved(IConnector connector);
	
}
