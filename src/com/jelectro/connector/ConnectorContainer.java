package com.jelectro.connector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.jelectro.ConnectionListener;
import com.jelectro.ConnectionListener.ConnectionEvent;
import com.jelectro.JElectro;
import com.jelectro.utils.WeakFireListeners;

 

public class ConnectorContainer {

	private static final Logger log = Logger.getLogger(ConnectorContainer.class);

	private final Map<ConnectorKey, IConnector> connectorMap;
	
	private WeakFireListeners<ConnectionListener> weakConectionListenerList;
	
	private final Object connectionEventLock = new Object();
	
	public ConnectorContainer() {
		connectorMap = new ConcurrentHashMap<ConnectorKey, IConnector>();
		weakConectionListenerList = new WeakFireListeners<>(ConnectionListener.class);
	}

	public void addConnector(IConnector connector) {
		connectorMap.put(connector.getKey(), connector);
		log.debug("In " + this + " Connector added " + connector.getKey());
		
		synchronized (connectionEventLock) {
			connectionEventLock.notifyAll();
		}
		// TODO shall be reworked with a cleaner async possibility 
		new Thread( () -> {
			weakConectionListenerList.getFireProxy().onConnectionEvent(ConnectionEvent.CONNECTION);
		}).start();
	}

	public void removeConnector(IConnector connector) {
		connectorMap.remove(connector.getKey());
		log.debug("In " + this + " Connector removed " + connector.getKey());
		
		synchronized (connectionEventLock) {
			connectionEventLock.notifyAll();
		}
		
		// TODO shall be reworked with a cleaner async possibility 
		new Thread( () -> {
			weakConectionListenerList.getFireProxy().onConnectionEvent(ConnectionEvent.DISCONNECTION);
		}).start();
	}

	public void addConnectionListener(ConnectionListener connectionListener) {
		weakConectionListenerList.addListener(connectionListener);
	}

	public IConnector getConnector(ConnectorKey connectorKey) {
		return connectorMap.get(connectorKey);
	}

	/**
	 * TODO implements a timeout ....
	 * @param minimalConnectionCount
	 * @return
	 */
	public int waitForActiveConnections(int minimalConnectionCount) {
		if (size()>=minimalConnectionCount ) return size() ;
		synchronized (connectionEventLock) {
			while (size()<minimalConnectionCount) {
				try {
					connectionEventLock.wait(JElectro.DEFAULT_GLOBAL_TIMEOUT);
				} catch (InterruptedException e) {
					
				}
			}
			return size();
		}
	}

	public int size() {
		return connectorMap.size();
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

}
