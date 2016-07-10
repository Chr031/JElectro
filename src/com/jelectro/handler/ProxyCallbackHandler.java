package com.jelectro.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jelectro.JElectroCallback;
import com.jelectro.node.Node;
import com.jelectro.node.NodePathList;

public class ProxyCallbackHandler extends ProxyHandler {
	
	
	protected final Map<Method,List<Integer>> callbackCache;
	
	public ProxyCallbackHandler(String stubName, Node node, NodePathList stubNodePaths) {

		super(stubName, node, stubNodePaths);
		callbackCache = new HashMap<Method, List<Integer>>();

	}
	
	public void bindRemoteInterfaceForCallbacks(Class<?> remoteInterface) {
		if (! remoteInterface.isInterface() )
			return ;
		
		
		Method[] methods = remoteInterface.getMethods();
		for(Method m : methods) {
			
			List<Integer> callbackPositionList = callbackCache.get(m);
			if (callbackPositionList == null) {
				callbackPositionList = new ArrayList<Integer>();
				callbackCache.put(m, callbackPositionList);
			} else 
				callbackPositionList.clear();
			
			for ( int i =0; i<m.getParameterTypes().length;i++) {
				Class<?> argClass = m.getParameterTypes()[i];
			
				if (JElectroCallback.class.isAssignableFrom(argClass) && argClass.isInterface()) {				
					callbackPositionList.add(i);
				}
			}
		}
		
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		List<Integer> callbackPositionList = callbackCache.get(method);
		if (callbackPositionList!= null) {
			for (Integer i : callbackPositionList) {
				args[i] = new StubCallbackArgument(args[i], node);
			}
		}
		
		return super.invoke(proxy, method, args);
	}
	
	
}
