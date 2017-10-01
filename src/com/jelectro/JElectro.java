package com.jelectro;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jelectro.connector.ConnectorKey;
import com.jelectro.connector.IConnector;
import com.jelectro.connector.multicast.MulticastConnector;
import com.jelectro.connector.multicast.MulticastConnector.ConnectionState;
import com.jelectro.exception.JElectroException;
import com.jelectro.exception.StubNameAlreadyExistsException;
import com.jelectro.node.Node;
import com.jelectro.node.NodeKey;
import com.jelectro.node.NodeService;
import com.jelectro.stubs.StubSet;
import com.jelectro.stubs.StubSetListener;

import tools.logger.Logger;

public class JElectro implements Closeable {
	/**
	 * 1 second of default stub timeout
	 */
	public static final long DEFAULT_STUB_TIMEOUT = 1000l;

	/**
	 * 1 second of default timeout
	 */
	public static final long DEFAULT_GLOBAL_TIMEOUT = 1000l;

	private static final Logger log = Logger.getLogger(JElectro.class);

	public static void setDebugMode(boolean debugMode) {
		Logger.getLogMode().setDebugMode(debugMode);
	}

	public static void setInfoMode(boolean infoMode) {
		Logger.getLogMode().setInfoMode(infoMode);
	}

	private final String nodeName;
	private final Node node;
	private final NodeService nodeService;

	private MulticastConnector multicastConnector;

	/**
	 * Creates a new instance of JElectro node with the given name.
	 * 
	 * @param nodeName
	 */
	public JElectro(String nodeName) {
		super();
		this.nodeName = nodeName;
		nodeService = new NodeService();

		this.node = new Node(nodeService.createNodeKey(this.nodeName));

	}

	public NodeKey getNodeKey() {
		return node.getNodeKey();
	}

	public int[] getOpenPorts() {
		return node.getOpenPorts();
	}

	/** see {@link #startLanDiscovery(int, int, int)} */
	public ConnectionState startLanDiscovery(int mutlicastPort, int serverListenerPort) throws IOException, JElectroException {
		return startLanDiscovery(mutlicastPort, serverListenerPort, serverListenerPort);
	}

	/**
	 * This method sends multicast messages to locate any {@link JElectro} nodes
	 * on a LAN. If none are found, it will open a port in order to allow new
	 * {@link JElectro} instances to connect to it.
	 * 
	 * @param mutlicastPort
	 * @param serverListenerPort
	 * @throws IOException
	 * @throws JElectroException
	 */
	public ConnectionState startLanDiscovery(int mutlicastPort, int serverListenerPortFrom, int serverListenerPortUpto)
			throws IOException, JElectroException {
		if (multicastConnector != null)
			multicastConnector.stop();
		multicastConnector = new MulticastConnector(mutlicastPort);
		multicastConnector.start();
		return multicastConnector.lookForExistingServersOrRegisterServer(this.node, serverListenerPortFrom, serverListenerPortUpto);
	}

	/**
	 * Open a port on the local instance and listen to incoming connections and
	 * messages.
	 * 
	 * @param port
	 * @throws IOException
	 * @throws JElectroException
	 */
	public JElectro listenTo(int port) throws IOException, JElectroException {
		node.listenTo(port);
		return this;
	}

	/**
	 * Connect to a remote instance located on host:port.
	 * 
	 * @param host
	 * @param port
	 * @throws IOException
	 * @throws JElectroException
	 */
	public JElectro connectTo(String host, int port) throws IOException, JElectroException {
		node.connectTo(host, port);
		return this;
	}

	public List<ConnectorKey> getActiveConnections() {
		List<ConnectorKey> keys = new ArrayList<ConnectorKey>();
		Iterator<IConnector> iter = node.getConnectorContainer().iterator();
		while (iter.hasNext()) {
			keys.add(iter.next().getKey());
		}
		return keys;
	}

	public void addConnectionListener(ConnectionListener connectionListener) {
		
		node.getConnectorContainer().addConnectionListener(connectionListener);
		
		
	}

	public <S> void bind(String stubName, S stubInstance) throws StubNameAlreadyExistsException {
		node.bind(stubName, stubInstance);
	}

	public void unbind(String stubName) throws JElectroException {
		node.unbind(stubName);
	}

	public <S> StubSet<S> lookup(String regexLocateString, Class<S> stubInterface) throws IOException, JElectroException {
		return lookup(regexLocateString, stubInterface, (StubSetListener<S>) null);
	}

	@SuppressWarnings("unchecked")
	public <S> StubSet<S> lookup(String regexLocateString, Class<S> stubInterface, StubSetListener<S> stubSetListener)
			throws IOException, JElectroException {
		return node.lookup(regexLocateString, stubInterface, stubSetListener);
	}

	public <S> StubSet<S> lookup(String regexLocateString, Class<S> stubInterface, StubSetListener<S>... stubSetListeners)
			throws IOException, JElectroException {
		return node.lookup(regexLocateString, stubInterface, stubSetListeners);
	}

	public <S> S lookupUnique(String regexLocateString, Class<S> stubInterface) throws IOException, JElectroException {
		return lookup(regexLocateString, stubInterface).get(0);
	}

	public void close() {
		try {
			node.close();
		} catch (InterruptedException e) {
			log.error("Close interrupted", e);
		}

		if (multicastConnector != null)
			multicastConnector.stop();

	}

}
