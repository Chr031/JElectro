package com.jelectro.connector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import tools.logger.Logger;

 

public class ConnectorContainer {

	private static final Logger log = Logger.getLogger(ConnectorContainer.class);

	private final Map<ConnectorKey, IConnector> connectorMap;

	public ConnectorContainer() {
		connectorMap = new ConcurrentHashMap<ConnectorKey, IConnector>();
	}

	public void addConnector(IConnector connector) {
		connectorMap.put(connector.getKey(), connector);
		log.debug("In " + this + " Connector added " + connector.getKey());

	}

	public void removeConnector(IConnector connector) {
		connectorMap.remove(connector.getKey());
		log.debug("In " + this + " Connector removed " + connector.getKey());
	}

	public IConnector getConnector(ConnectorKey connectorKey) {
		return connectorMap.get(connectorKey);
	}

	/**
	 * Returns an iterator over all present connectors.
	 * 
	 * TODO this method should return a copy of all the present connectors :
	 * Currently this is not thread safe.
	 * 
	 * @return
	 */
	public Iterator<IConnector> iterator() {
		return connectorMap.values().iterator();
	}
	
	/**
	 * Returns a copy of all connector keys.
	 * @return
	 */
	public List<ConnectorKey> getConnectorKeyList() {
		List<ConnectorKey> keys = new ArrayList<ConnectorKey>(connectorMap.keySet());
		return keys;
	}
	
	
	/**
	 * Returns a copy of all connector keys.
	 * @return
	 */
	public Set<ConnectorKey> getConnectorKeySet() {
		Set<ConnectorKey> keys = new HashSet<ConnectorKey>(connectorMap.keySet());
		
		return keys;
	}

	public void closeAllConnectors() {
		for (IConnector c : connectorMap.values()) {
			c.close();

		}

	}

	public int size() {
		return connectorMap.size();
	}

}
