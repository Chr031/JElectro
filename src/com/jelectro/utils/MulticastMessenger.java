package com.jelectro.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class MulticastMessenger<M> {

	private static final Logger log = Logger.getLogger(MulticastMessenger.class);

	private static final byte[] MESSAGE_HEADER = new byte[] { 1, 2, 3, 4, 5, 6 };
	private final FireListeners<MulticastResponseListener<M>> responseListeners;
	private final InetAddress group;
	private final MulticastSocket mSocket;
	private final int port;

	private final ISerializer serializer;
	private final MessageReceiver messageReceiver;

	public MulticastMessenger(int port) throws IOException {
		this.port = port;
		responseListeners = new FireListeners<MulticastResponseListener<M>>();
		group = InetAddress.getByName("228.5.6.7");
		mSocket = new MulticastSocket(port);
		mSocket.joinGroup(group);
		serializer = new Serializer();
		messageReceiver = new MessageReceiver();
		messageReceiver.start();
	}

	public void send(M messageToSend) throws IOException {

		byte[] msg = serializer.serialize(messageToSend);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.write(MESSAGE_HEADER);
		// dos.writeInt(msg.length);
		dos.write(msg);
		dos.flush();
		dos.close();
		byte[] buf = baos.toByteArray();
		DatagramPacket msgPacket = new DatagramPacket(buf, buf.length, group, port);

		mSocket.send(msgPacket);

	}

	public void addMulticastResponseListener(MulticastResponseListener<M> mrl) {
		responseListeners.addListener(mrl);

	}

	public void close() {
		messageReceiver.setActive(false);
		mSocket.close();
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	private class MessageReceiver extends Thread {

		private volatile boolean active;

		private MessageReceiver() {
			super("Multicast message listener");
			setActive(true);
		}

		public void run() {

			byte[] buff = new byte[1000];
			while (isActive()) {
				DatagramPacket msgPacket = new DatagramPacket(buff, buff.length);
				try {
					mSocket.receive(msgPacket);
					if (Arrays.equals(Arrays.copyOfRange(buff, 0, MESSAGE_HEADER.length), MESSAGE_HEADER)) {

						M msg = serializer.unserialize(Arrays.copyOfRange(buff, MESSAGE_HEADER.length, msgPacket.getLength()));
						if (responseListeners.isFireProxyReady())
							responseListeners.getFireProxy().onResponse(msg);

					} else {
						log.debug("Message not understandable : " + new String(buff, 0, msgPacket.getLength()));
					}

				} catch (Throwable e) {
					if (e instanceof SocketException && e.getMessage().equalsIgnoreCase("socket closed")) {
						log.debug("Multicast socket closed");
						mSocket.close();
						setActive(false);
					} else
						log.error("Unexpected error while receiving multicast message", e);
				}
			}
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}
	}

	public static interface MulticastResponseListener<M> {
		void onResponse(M messageReceived) throws Exception;
	}
}
