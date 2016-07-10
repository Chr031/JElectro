package com.jelectro.handler;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Random;

import com.jelectro.exception.JElectroException;
import com.jelectro.node.Node;
import com.jelectro.node.NodePath;
import com.jelectro.node.NodePathList;
import com.jelectro.stubs.IStub;
import com.jelectro.stubs.StubProxyFactory;

public class StubCallbackArgument implements Serializable {

	private static final long serialVersionUID = 6120353262544103889L;

	private transient static final Random rand = new Random();

	private final String stubName;

	public StubCallbackArgument(Object stub, Node node) throws JElectroException {
		stubName = "JElectroCallback" + rand.nextInt();
		node.bind(stubName, stub);
	}

	public String getStubName() {
		return stubName;
	}

	protected void unbind(Node node) throws JElectroException {
		node.unbind(stubName);
	}

	/**
	 * TODO Refactor this method in order to call a method from {@link StubProxyFactory}
	 * @param callbackInterface
	 * @param node
	 * @param path
	 * @return
	 */
	public Object getProxy(Class<?> callbackInterface, Node node, NodePath path) {
		ProxyHandler handler = new ProxyHandler(stubName, node, new NodePathList(path));
		
		return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { callbackInterface, IStub.class }, handler);
	}

}
