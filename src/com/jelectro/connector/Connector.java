package com.jelectro.connector;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.jelectro.exception.JElectroException;
import com.jelectro.message.Message;
import com.jelectro.message.MessageTransporter;
import com.jelectro.node.NodeKey;


/**
 * NOS for native Object serialization !!
 * @author Blue
 *
 */
public class Connector implements IConnector  {

	private final Socket socket;
	private NodeKey localNodeKey;
	private NodeKey remoteNodeKey;

	private ConnectorKey connectorKey;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private InMessageRunner runner;

	private final IConnectorListener listener;

	public Connector(Socket socket, NodeKey nodeKey, IConnectorListener listener) {
		this.localNodeKey = nodeKey;
		this.socket = socket;
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see com.jelectro.connector.IConnector#getKey()
	 */
	@Override
	public ConnectorKey getKey() {
		return connectorKey;
	}

	/* (non-Javadoc)
	 * @see com.jelectro.connector.IConnector#init()
	 */
	@Override
	public void init() throws IOException, JElectroException {

		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());

		oos.writeObject(localNodeKey);
		oos.flush();
		try {
			remoteNodeKey = (NodeKey) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new JElectroException("Connector can not be initialized", e);
		}
		
		connectorKey = new ConnectorKey(localNodeKey, remoteNodeKey);
		oos.writeBoolean(true);
		oos.flush();
		boolean remoteConnectionReady = ois.readBoolean();
		// at this point the connector should be ready 
		listener.onInit(this);
		
		if (remoteConnectionReady) {
			runner = new InMessageRunner();
			final Thread runnerThread = new Thread(runner);
			runnerThread.setName(String.format("JElectroConnector-%1$s-%2$s",localNodeKey.getName(), remoteNodeKey.getName()));
			runnerThread.start();
			listener.onStart(this);
		} else
			throw new JElectroException("Remote connection not ready !!!");
	}

	/* (non-Javadoc)
	 * @see com.jelectro.connector.IConnector#sendMessageTransporter(com.jelectro.message.MessageTransporter)
	 */
	@Override
	public synchronized <M extends Message>  void sendMessageTransporter(MessageTransporter<M> transporter) throws IOException {
		oos.writeObject(transporter);
		
		oos.flush();
		//oos.reset();
	}

	private class InMessageRunner implements Runnable {

		private volatile boolean active;

		@Override
		public void run() {
			try {
				setActive(true);
				while (active) {

					@SuppressWarnings("unchecked")
					MessageTransporter<? extends Message> transporter = (MessageTransporter<? extends Message>) ois.readObject();
					listener.onMessageTransporterReceive(Connector.this, transporter);
					
				}
			} catch (EOFException eofe) {
				try {
					socket.close();
				} catch (IOException ioe) {
					listener.onError(Connector.this, ioe);
				}
			} catch (Exception e) {
				if (!(e instanceof SocketException && e.getMessage().equalsIgnoreCase("socket closed")))
					listener.onError(Connector.this, e);
				try {
					socket.close();
				} catch (IOException ioe) {
					listener.onError(Connector.this, ioe);
				}

			} finally {
				listener.onClose(Connector.this);
			}

		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

	}

	/* (non-Javadoc)
	 * @see com.jelectro.connector.IConnector#close()
	 */
	@Override
	public void close() {
		try {
			if (runner != null)
				runner.setActive(false);

			socket.close();
		} catch (IOException ioe) {
			listener.onError(Connector.this, ioe);
		} finally {
			listener.onClose(Connector.this);
		}

	}

}
