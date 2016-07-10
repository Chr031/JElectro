package com.jelectro.utils;

import java.io.Serializable;

class HelloMulticast implements Serializable {

	private static final long serialVersionUID = 8355951028546768507L;

	private int port;
	private String hello;

	public HelloMulticast(int port, String hello) {
		super();
		this.setPort(port);
		this.setHello(hello);
	}

	int getPort() {
		return port;
	}

	void setPort(int port) {
		this.port = port;
	}

	String getHello() {
		return hello;
	}

	void setHello(String hello) {
		this.hello = hello;
	}

}