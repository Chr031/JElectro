package com.jelectro.connector.multicast;

import java.io.IOException;
import java.net.BindException;
import java.net.UnknownHostException;
import java.util.function.BooleanSupplier;

import org.apache.log4j.Logger;

import com.jelectro.connector.IConnector;
import com.jelectro.exception.JElectroException;
import com.jelectro.node.INodeListener;
import com.jelectro.node.Node;
import com.jelectro.utils.MulticastMessenger;
import com.jelectro.utils.MulticastMessenger.MulticastResponseListener;

public class MulticastConnector {

	private static final Logger log = Logger.getLogger(MulticastConnector.class);
	
	private final int port;
	private MulticastMessenger<ConnectionLocatorMessage> multicastMessenger;
	private MulticastResponseListener<ConnectionLocatorMessage> responseListener;

	private BooleanSupplier isConnected;
	private final Object waitForConnectionLock;
	private ConnectionInfo currentConnectionInfo;

	public MulticastConnector(int port) {

		this.port = port;
		isConnected = ()->false;
		waitForConnectionLock = new Object();
		
	}

	public void start() throws IOException {
		if (multicastMessenger != null)
			multicastMessenger.close();
		multicastMessenger = new MulticastMessenger<ConnectionLocatorMessage>(port);

	}

	public void stop() {

		if (multicastMessenger != null)
			multicastMessenger.close();

	}

	public ConnectionState lookForExistingServersOrRegisterServer(Node node, int serverListenerPortFrom, int serverListenerPortUpTo) throws IOException, JElectroException {
		if (multicastMessenger == null)
			throw new JElectroException("Multicast connector is not instanciated");
		
		isConnected = ()->node.getConnectorContainer().size()>0;
		// init the server port on the node :
		for (int port = serverListenerPortFrom; port <= serverListenerPortUpTo; port++) {
			try {
				node.listenTo(port);
				currentConnectionInfo = new ConnectionInfo(port);
				break;
			} catch (BindException be) {
				log.debug("Port " + port + " is not available : " + be.getMessage());
			}

		}
		
		ConnectionState state = new ConnectionState(node);
		responseListener = new MulticastConnectorResponseListener(node, state);
		multicastMessenger.addMulticastResponseListener(responseListener);

		// Notify my server entry :
		multicastMessenger.send(new ConnectionLocatorMessage(node.getNodeKey(), currentConnectionInfo));

			
		state.setReachable(() -> true);
		return state;

	}

	private class MulticastConnectorResponseListener implements MulticastResponseListener<ConnectionLocatorMessage> {

		private final Node node;		
		private Boolean isServer = true;
		

		public MulticastConnectorResponseListener(Node node, ConnectionState state) {
			this.node = node;			
		}

		@Override
		public synchronized void onResponse(ConnectionLocatorMessage message) throws UnknownHostException, IOException, JElectroException {

			if (message.getSenderNodeKey().equals(node.getNodeKey()) || !isServer)
				return;

			log.debug("Received Random number " + message.getConnectionInfo().getRandomNumber() +
					" vs " + currentConnectionInfo.getRandomNumber() );
			
			if (message.getConnectionInfo().getRandomNumber() < currentConnectionInfo.getRandomNumber()) {
				// connection to the node that has sent the message
				node.connectTo(message.getConnectionInfo().getAddress(), message.getConnectionInfo().getPort());
				log.info("New connection done ");
				isServer = false;
			} else {
				if (message.getConnectionInfo().getRandomNumber() == currentConnectionInfo.getRandomNumber()) {
					// regenerate a new randomNumber for the current connection
					// info and re-send it :
					log.debug("Regeneration ");
					currentConnectionInfo.defineRandomNumber();
				}
				multicastMessenger.send(new ConnectionLocatorMessage(node.getNodeKey(), currentConnectionInfo));
				log.debug("I am the server ");
			}
				

		}

	}

	public class ConnectionState {

		private BooleanSupplier reachable = ()-> false;
		private BooleanSupplier connected = ()-> false;

		private final Node node;
		private final INodeListener nodeListener;

		public ConnectionState(final Node node) {
			this.node = node;
			connected = () ->node.getConnectorContainer().size() >0;
			nodeListener = new INodeListener() {
				
							
				@Override
				public void onConnectorRemoved(IConnector connector) {					
					synchronized (waitForConnectionLock) {
						waitForConnectionLock.notifyAll();
					}
				}
				
				@Override
				public void onConnectorAdded(IConnector connector) {
					synchronized (waitForConnectionLock) {
						waitForConnectionLock.notifyAll();
					}
				}
			};
			node.addNodeListener(nodeListener);
			
		}

		public boolean isConnected() throws InterruptedException, UnknownHostException, IOException, JElectroException {
			if (!connected.getAsBoolean()) {
				synchronized (waitForConnectionLock) {
					while (!connected.getAsBoolean()) {
						waitForConnectionLock.wait(10);
					}
				}
			}
			return connected.getAsBoolean();
		}

		public boolean isReachable() {
			return reachable.getAsBoolean();
		}

		private void setReachable(BooleanSupplier reachable) {
			this.reachable = reachable;
		}

	}

}
