package com.jelectro.connector;

import com.jelectro.message.Message;
import com.jelectro.message.MessageTransporter;

public interface IConnectorListener {

	void onInit(IConnector connector);
	
	void onStart(IConnector connector);
	
	void onClose(IConnector connector);
	
	void onError(IConnector connector, Throwable error);
	
	<M extends Message> void onMessageTransporterReceive(IConnector connector, MessageTransporter<M> transporter);

	

}
