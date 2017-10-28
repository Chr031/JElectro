package com.jelectro.connector;

import java.io.IOException;

import com.jelectro.exception.JElectroException;
import com.jelectro.message.Message;
import com.jelectro.message.MessageTransporter;

public interface IConnector {

	ConnectorKey getKey();

	String getRemoteHost();
	
	int getRemotePort();
	
	/**
	 * This method initialize the thread to listen to the incoming messages. It
	 * should also initialize the object's streams based on the socket.
	 * 
	 * @throws IOException
	 * @throws JElectroException
	 * 
	 */
	void init() throws IOException, JElectroException;

	<M extends Message> void sendMessageTransporter(MessageTransporter<M> transporter) throws IOException;

	void close();

}