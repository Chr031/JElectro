package com.jelectro.connector.multicast;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

class ConnectionInfo implements Serializable {

	private static final long serialVersionUID = 5593597119687602376L;

	private int randomNumber;
	
	private final String address;

	private final int port;

	protected ConnectionInfo(String address, int port) {
		super();
		this.address = address;
		this.port = port;
		defineRandomNumber() ;
	}
	
	/**
	 * Creates a instance of this class where the address is the local lan
	 * address of the computer running the JVM. <br>
	 * TODO : check the behavior in case of multiple network interfaces.
	 * 
	 * 
	 * @param port
	 * @throws UnknownHostException 
	 */
	public ConnectionInfo(int port) throws UnknownHostException {
		this(InetAddress.getLocalHost().getHostAddress(), port);		
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public int getRandomNumber() {
		return randomNumber;
	}

	public void defineRandomNumber() {
		this.randomNumber = new Random(System.nanoTime()).nextInt();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionInfo other = (ConnectionInfo) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
}