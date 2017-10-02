package com.jelectro.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class WeakFireListeners<L> {

	private final Map<L, Void> lMap;
	private volatile L fireProxy;
	private volatile boolean fireProxyReady;

	/**
	 * <p>
	 * Check that L represents an interface and not something else.
	 * </p>
	 * <p>
	 * Check that when the proxy is used, at least one listener has been added
	 * to it for the initialisation of the fire listener proxy.
	 * <p>
	 * <p>
	 * Note that this class is thread safe according to the call to
	 * {@link Collections#synchronizedMap} for the creation of the
	 * {@link WeakHashMap}
	 */
	public WeakFireListeners() {
		lMap = Collections.synchronizedMap(new WeakHashMap<L, Void>());
	}

	public WeakFireListeners(Class<L> listenerClass) {
		this();
		initFireProxy(listenerClass);
	}

	@SuppressWarnings("unchecked")
	public void addListener(L listener) {
		if (fireProxy == null && listener != null) {
			initFireProxy((Class<L>) listener.getClass());
		}
		lMap.put(listener, null);
	}

	@SuppressWarnings("unchecked")
	public void addListeners(L... listeners) {
		if (fireProxy == null && listeners.length > 0) {
			int i = 0;
			while (i < listeners.length && listeners[i] == null)
				i++;
			if (i < listeners.length)
				initFireProxy((Class<L>) listeners[i].getClass());
		}

		for (L l : listeners) {
			lMap.put(l, null);
		}
	}

	public Set<L> getListeners() {
		return lMap.keySet();
	}

	@SuppressWarnings("unchecked")
	protected void initFireProxy(Class<L> clazz) {
		InvocationHandler handler = new FireInvocationHandler();
		fireProxy = (L) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.isInterface() ? new Class[] { clazz } : clazz.getInterfaces(), handler);
		fireProxyReady = true;
	}

	public boolean isFireProxyReady() {
		return fireProxyReady;
	}

	/**
	 * See {@link #initFireProxy(Class)}
	 * 
	 * @return a {@link Proxy} instance of L that can trigger all listener's
	 *         events or null if {@link #initFireProxy(Class)} has not been
	 *         called.
	 */
	public L getFireProxy() {
		return fireProxy;
	}

	private class FireInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			for (L l : getListeners()) {
				method.invoke(l, args);
			}
			return null;
		}

	}

	/**
	 * return the current number of listeners present in this listener container. 
	 * @return
	 */
	public int size() {
		return lMap.size();
	}

}
