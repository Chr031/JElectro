package com.jelectro.stubs;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.jelectro.handler.ProxyCallbackHandler;
import com.jelectro.handler.ProxyHandler;
import com.jelectro.node.Node;
import com.jelectro.node.NodePathList;

public class StubProxyFactory {

	public static <S> S createStubProxy(Node node, String stubName, Class<S> stubInterface, NodePathList stubNodePath) {

		ProxyCallbackHandler proxyHandler = new ProxyCallbackHandler(stubName, node, stubNodePath);
		proxyHandler.bindRemoteInterfaceForCallbacks(stubInterface);
		@SuppressWarnings("unchecked")
		S proxy = (S) Proxy.newProxyInstance(stubInterface.getClassLoader(), new Class[] { stubInterface, IStub.class }, proxyHandler);
		return proxy;
	}

	/**
	 * 
	 * Creates and instantiates the proxy and the handler
	 * 
	 * 
	 * Returns a tuple of {@link InvocationHandler} and {@link Proxy}
	 * 
	 * @param node
	 * @param stubName
	 * @param stubInterface
	 * @param stubNodePath
	 * @return a tuple of {@link InvocationHandler} and {@link Proxy}
	 */
	public static <S> StubProxyHandler<S> createStubProxyHandler(Node node, String stubName, Class<S> stubInterface, NodePathList stubNodePaths) {

		ProxyCallbackHandler proxyHandler = new ProxyCallbackHandler(stubName, node, stubNodePaths);
		proxyHandler.bindRemoteInterfaceForCallbacks(stubInterface);
		@SuppressWarnings("unchecked")
		S proxy = (S) Proxy.newProxyInstance(stubInterface.getClassLoader(), new Class[] { stubInterface, IStub.class }, proxyHandler);
		return new StubProxyHandler<S>(proxyHandler, proxy);
	}

	
	
	public static class StubProxyHandler<S> {
		private final ProxyHandler proxyHandler;
		private final S stub;

		protected StubProxyHandler(ProxyHandler proxyHandler, S stub) {
			super();
			this.proxyHandler = proxyHandler;
			this.stub = stub;
		}

		public ProxyHandler getProxyHandler() {
			return proxyHandler;
		}

		public S getStub() {
			return stub;
		}

	}

}
