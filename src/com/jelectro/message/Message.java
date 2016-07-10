package com.jelectro.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

import com.jelectro.processor.MessageProcessorManager;

public abstract class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final transient Random rand = new Random(System.nanoTime());

	protected final long messageId;
	
	protected Message() {
		this(rand.nextLong());
	}

	protected Message(long messageId) {
		this.messageId = messageId;
	}

	public abstract <M extends Message> void process(MessageProcessorManager messageProcessor, MessageTransporter<M> transporter) throws InterruptedException, IOException;

	public long getMessageId() {
		return messageId;
	}

	protected byte[] serialize(Object o) throws IOException {
	
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.flush();
		oos.close();
		final byte[] serial = baos.toByteArray();
		return serial;
	}

	protected Object unserialize(byte[] serial) throws IOException, ClassNotFoundException {
	
		ByteArrayInputStream bais = new ByteArrayInputStream(serial);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	@Override
	public String toString() {
		return "Message [messageId=" + messageId + "]";
	}

	/** <code>
	public void write(MessageOutStream out) throws IOException {
		out.writeLong(messageId);
		
	}

	@Override
	public void read(MessageInStream in) throws IOException, ClassNotFoundException {
		messageId = in.readLong();
		
	}
	</code>*/

}
