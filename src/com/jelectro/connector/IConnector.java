package com.jelectro.connector;

import java.io.IOException;

import com.jelectro.exception.JElectroException;
import com.jelectro.message.Message;
import com.jelectro.message.MessageTransporter;

public interface IConnector {

	public abstract ConnectorKey getKey();

	/**
	 * This method initialize the thread to listen to the incoming messages. It
	 * should also initialize the object's streams based on the socket.
	 * 
	 * @throws IOException
	 * @throws JElectroException
	 * 
	 */
	public abstract void init() throws IOException, JElectroException;

	public abstract <M extends Message> void sendMessageTransporter(MessageTransporter<M> transporter) throws IOException;

	public abstract void close();

}