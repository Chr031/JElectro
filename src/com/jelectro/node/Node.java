package com.jelectro.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.blue.tools.utils.WeakFireListeners;

import com.jelectro.JElectro;
import com.jelectro.connector.Connector;
import com.jelectro.connector.ConnectorContainer;
import com.jelectro.connector.ConnectorKey;
import com.jelectro.connector.IConnector;
import com.jelectro.connector.IConnectorListener;
import com.jelectro.exception.JElectroException;
import com.jelectro.exception.PathNotAvailableException;
import com.jelectro.exception.StubNameAlreadyExistsException;
import com.jelectro.message.ErrorMessage;
import com.jelectro.message.LookupMessage;
import com.jelectro.message.LookupResultMessage;
import com.jelectro.message.Message;
import com.jelectro.message.MessageTransporter;
import com.jelectro.message.response.IMessageResponse;
import com.jelectro.message.response.MessageResponseContainer;
import com.jelectro.message.response.MessageResponseMulti;
import com.jelectro.message.response.MessageResponseSingle;
import com.jelectro.processor.ErrorMessageProcessor;
import com.jelectro.processor.ExecuteMessageProcessor;
import com.jelectro.processor.LookupMessageProcessor;
import com.jelectro.processor.MessageProcessorManager;
import com.jelectro.stubs.FutureStubSet;
import com.jelectro.stubs.LookupResultStubProducer;
import com.jelectro.stubs.StubContainer;
import com.jelectro.stubs.StubSet;
import com.jelectro.stubs.StubSet.IStubSetListener;

import tools.logger.Logger;

/**
 * Kind of the main class of this project.
 * 
 * 
 * @author xneb
 * 
 */
public class Node implements IConnectorListener {

	private static final Logger log = Logger.getLogger(Node.class);

	private final ConnectorContainer connectorContainer;
	private final NodeKey nodeKey;

	private final List<ServerSocketThread> serverSocketThreads;
	private final ExecutorService executor;

	private final MessageProcessorManager messageProcessorManager;
	private final MessageResponseContainer messageResponseContainer;

	private final StubContainer stubContainer;

	private final WeakFireListeners<INodeListener> nodeListeners;

	public Node(final NodeKey nodeKey) {
		this.nodeKey = nodeKey;
		this.nodeListeners = new WeakFireListeners<INodeListener>();
		this.connectorContainer = new ConnectorContainer();
		this.executor = Executors.newCachedThreadPool(new ThreadFactory() {

			private final AtomicInteger aInt = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("JElectroNodeExecutor-" + nodeKey.getName() + "-" + aInt.getAndIncrement());
				return t;

			}
		});
		serverSocketThreads = new ArrayList<ServerSocketThread>();

		this.stubContainer = new StubContainer();

		messageResponseContainer = new MessageResponseContainer();

		this.messageProcessorManager = new MessageProcessorManager();
		messageProcessorManager.setExecuteMessageProcessor(new ExecuteMessageProcessor(this));
		messageProcessorManager.setErrorMessageProcessor(new ErrorMessageProcessor(this));
		messageProcessorManager.setLookupMessageProcessor(new LookupMessageProcessor(this));

	}

	public NodeKey getNodeKey() {
		return this.nodeKey;
	}

	public void addNodeLIstener(INodeListener nl) {
		nodeListeners.addListener(nl);
	}

	public ConnectorContainer getConnectorContainer() {
		return connectorContainer;
	}

	public MessageResponseContainer getMessageResponseContainer() {
		return messageResponseContainer;
	}

	/**
	 * Return the reference of the container that owns all the stub instances
	 * present in this node.
	 * 
	 * @return
	 */
	public StubContainer getStubContainer() {
		return stubContainer;
	}

	public void bind(String stubName, Object stubInstance) throws StubNameAlreadyExistsException {
		if (stubContainer.contains(stubName))
			throw new StubNameAlreadyExistsException(stubName);
		stubContainer.addStub(stubName, stubInstance);

	}

	public Object unbind(String stubName) throws JElectroException {
		if (!stubContainer.contains(stubName))
			throw new JElectroException(String.format("No stub with name '%s' are present", stubName));
		return stubContainer.removeStub(stubName);

	}

	public <S> StubSet<S> lookup(String regexLookupString, Class<S> stubInterface, IStubSetListener<S>... stubSetListeners)
			throws IOException, JElectroException {

		final LookupMessage locateMessage = new LookupMessage(regexLookupString, stubInterface);
		final MessageResponseMulti<LookupResultMessage> response = new MessageResponseMulti<LookupResultMessage>(
				locateMessage.getMessageId());
		final LookupResultStubProducer<S> stubProducer = new LookupResultStubProducer<S>(this, stubInterface, response);
		final StubSet<S> futureStubSet = new FutureStubSet<S>(stubProducer);
		if (stubSetListeners != null && stubSetListeners.length > 0 && stubSetListeners[0] != null)
			futureStubSet.addStubSetListener(stubSetListeners);

		// TODO see if dummy local binding is a option !
		/**
		 * <code>// look for local dummy binding
		for (String stubName : stubContainer.getPublicMatchingStubNames(regexLookupString, stubInterface)) {
			//stubProducer.addElement(stubContainer.getStub(stubName, stubInterface));
			
		}</code>
		 */

		// send the message after the FutureStubSet instantiation in order not
		// to loose any messages.
		this.broadcastMessageRegisterResponse(locateMessage, response);

		return futureStubSet;

	}

	/**
	 * Connects to a remote instance. Starts a connector.
	 * 
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws JElectroException
	 */
	public void connectTo(String host, int port) throws UnknownHostException, IOException, JElectroException {
		Socket socket = new Socket(host, port);
		// IConnector connector = new COSConnector(socket, nodeKey, this);
		IConnector connector = new Connector(socket, nodeKey, this);
		connector.init();

	}

	public void listenTo(int port) throws IOException {
		if (isPortOpen(port)) return; 
				
		ServerSocketThread serverThread = new ServerSocketThread("JElectroServer-" + port, port);
		serverThread.start();
		serverSocketThreads.add(serverThread);
	}

	public void close() throws InterruptedException {
		connectorContainer.closeAllConnectors();
		for (ServerSocketThread sst : serverSocketThreads) {
			try {
				sst.close();
			} catch (IOException e) {
				onError(null, e);
			}
		}
		executor.shutdown();
		boolean executorClosed = executor.awaitTermination(JElectro.DEFAULT_GLOBAL_TIMEOUT, TimeUnit.MILLISECONDS);
		if (!executorClosed)
			onError(null, new JElectroException("Executor has not been shutdown properly"));
	}

	/**
	 * TODO this method should be synchronized in some way with
	 * {@link #serverSocketThreads};
	 * 
	 * @return the list of the current open ports.
	 */
	public int[] getOpenPorts() {
		int[] openPorts = new int[serverSocketThreads.size()];
		for (int i = 0; i < serverSocketThreads.size(); i++) {
			openPorts[i] = serverSocketThreads.get(i).serverSocketPort;
		}
		return openPorts;
	}

	/**
	 * checks if a port is already open on this node
	 * 
	 * @param port
	 * @return
	 */
	public boolean isPortOpen(int port) {

		for (ServerSocketThread sst : serverSocketThreads) {
			if (sst.serverSocketPort == port)
				return true;
		}
		return false;
	}

	/**
	 * Use this method only if the response is different from the original
	 * message.
	 * 
	 * @param message
	 * @param response
	 * @param destinationPath
	 * @throws IOException
	 * @throws JElectroException
	 */
	public <M extends Message, R extends Message> void sendMessageRegisterResponse(M message, MessageResponseSingle<R> response,
			NodePath destinationPath) throws IOException, JElectroException {
		messageResponseContainer.registerResponse(response);
		MessageTransporter<M> transporter = new MessageTransporter<M>(destinationPath, message);
		forwardMessage(transporter);
	}

	/**
	 * Broadcast the message to all adjacent connectors. Use this method only if
	 * the response is different from the original message.
	 * 
	 * @param message
	 * @param response
	 * @param destinationPath
	 * @throws IOException
	 * @throws JElectroException
	 */
	public <M extends Message, R extends Message> void broadcastMessageRegisterResponse(M message, IMessageResponse<R> response)
			throws IOException, JElectroException {
		messageResponseContainer.registerResponse(response);

		for (ConnectorKey cKey : getConnectorContainer().getConnectorKeyList()) {
			MessageTransporter<M> transporter = new MessageTransporter<M>(new NodePath(this.getNodeKey(), cKey.getRemoteNodeKey()),
					message);
			forwardMessage(transporter);
		}
	}

	/**
	 * Use this method if the response is the same as the original message.
	 * 
	 * @param message
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws JElectroException
	 */
	public <M extends Message> IMessageResponse<M> sendMessageWithResponse(M message, NodePath path) throws IOException, JElectroException {
		IMessageResponse<M> response = messageResponseContainer.createMessageResponseSingle(message);
		MessageTransporter<M> transporter = new MessageTransporter<M>(path, message);
		forwardMessage(transporter);
		return response;
	}

	public <M extends Message> void sendMessageSafe(M message, NodePath path) {
		MessageTransporter<M> transporter = new MessageTransporter<M>(path, message);
		safeForwardMessage(transporter);
	}

	@Override
	public void onInit(IConnector connector) {
		connectorContainer.addConnector(connector);
		log.info("In " + connectorContainer + " connector added " + connector.getKey());

	}

	@Override
	public void onStart(IConnector connector) {
		if (nodeListeners.isFireProxyReady())
			nodeListeners.getFireProxy().onConnectorAdded(connector);

	}

	@Override
	public void onClose(IConnector connector) {
		connectorContainer.removeConnector(connector);
		log.info("In " + connectorContainer + " connector removed " + connector.getKey());

		if (nodeListeners.isFireProxyReady())
			nodeListeners.getFireProxy().onConnectorRemoved(connector);

	}

	@Override
	public <M extends Message> void onMessageTransporterReceive(IConnector connector, MessageTransporter<M> transporter) {
		boolean isAtDestination = transporter.isDestination(nodeKey);
		if (isAtDestination) {
			processMessage(transporter);
		} else {
			safeForwardMessage(transporter);
		}

	}

	@Override
	public void onError(IConnector connector, Throwable error) {
		if (connector != null)
			log.error("Error on connector " + connector.getKey(), error);
		else
			log.error("Unforwardable error : ", error);

	}

	private <M extends Message> void processMessage(final MessageTransporter<M> transporter) {
		Runnable processorThread = new Runnable() {
			public void run() {
				try {
					messageProcessorManager.processMessage(transporter);
				} catch (Throwable error) {
					reverseMessageOnError(transporter, error);
				}
			}
		};

		executor.execute(processorThread);
	}

	private <M extends Message> void forwardMessage(MessageTransporter<M> transporter) throws IOException, JElectroException {

		NodeKey nextNodeKey = transporter.getNextNode(nodeKey);
		IConnector connector = connectorContainer.getConnector(new ConnectorKey(nodeKey, nextNodeKey));

		boolean isMessageSent = false;
		Throwable error = null;
		if (connector != null) {
			try {
				connector.sendMessageTransporter(transporter);
				isMessageSent = true;
			} catch (Throwable t) {
				isMessageSent = false;
				error = t;
			}
		}
		if (!isMessageSent) {
			JElectroException ex;
			if (connector == null) {
				ex = new PathNotAvailableException(String.format("Path not found : [%1$s - %2$s] ", nodeKey, nextNodeKey));
			} else {
				ex = new PathNotAvailableException(
						String.format("Error on sending message between : [%1$s - %2$s] ", nodeKey, nextNodeKey), error);
			}

			if (!(transporter.getMessage() instanceof ErrorMessage)) {
				reverseMessageOnError(transporter, ex);
			} else {
				throw ex;
			}
		}
	}

	private <M extends Message> void safeForwardMessage(MessageTransporter<M> transporter) {

		try {
			forwardMessage(transporter);
		} catch (IOException e) {
			onError(null, e);
		} catch (JElectroException e) {
			onError(null, e);
		}

	}

	private <M extends Message> void reverseMessageOnError(MessageTransporter<M> transporter, Throwable error) {
		M m = transporter.getMessage();
		ErrorMessage<M> errorMessage = new ErrorMessage<M>(m, error);
		MessageTransporter<ErrorMessage<M>> reverse = new MessageTransporter<ErrorMessage<M>>(transporter.getNodePath().reverse(),
				errorMessage);
		if (reverse.getDestination().equals(nodeKey)) {
			// message error has to be processed somehow by the processor !
			try {
				messageProcessorManager.processMessage(reverse);
			} catch (Throwable t) {
				onError(null, t);
			}
		} else {
			safeForwardMessage(reverse);
		}

	}

	private class ServerSocketThread extends Thread {

		private final int serverSocketPort;
		private final ServerSocket serverSocket;
		private volatile boolean active;

		public ServerSocketThread(String threadName, int port) throws IOException {
			super(threadName);
			this.serverSocketPort = port;
			this.serverSocket = new ServerSocket(port);
		}

		protected void close() throws IOException {
			active = false;
			this.interrupt();
			serverSocket.close();
		}

		public void run() {
			active = true;
			while (active) {
				final Socket socket;
				IConnector connector = null;
				try {
					socket = serverSocket.accept();
					connector = new Connector(socket, nodeKey, Node.this);
					connector.init();
				} catch (SocketException se) {
					if (!se.getMessage().matches("socket (is )?closed")) {
						onError(connector, se);
					}
					active = false;
				} catch (Throwable t) {
					onError(connector, t);
				}
			}
		}
	}

}
