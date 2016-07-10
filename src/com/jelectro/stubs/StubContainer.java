package com.jelectro.stubs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class StubContainer {

	private final Map<String, Object> stubMap;

	public StubContainer() {
		stubMap = new ConcurrentHashMap<String, Object>();
	}

	/**
	 * Add a stub to this container. Returns null or the previous binded
	 * instance.
	 * 
	 * @param name
	 * @param stub
	 * @return
	 */
	public Object addStub(String name, Object stub) {

		return stubMap.put(name, stub);

	}

	public Object getStub(String name) {
		return stubMap.get(name);
	}

	/**
	 * Returns the stub present in this container that matches the given
	 * interface. Returns null if no stubs with the given name and from the
	 * given interface is present.
	 * 
	 * @param name
	 * @param stubInterface
	 * @return
	 */

	
	@SuppressWarnings("unchecked")
	public <S> S getStub(String name, Class<S> stubInterface) {

		Object stub = getStub(name);

		if (stubInterface.isAssignableFrom(stub.getClass())) {
			return (S) stub;
		}
		return null;

	}

	public Object removeStub(String name) {
		return stubMap.remove(name);
	}

	public boolean contains(String name) {
		return stubMap.containsKey(name);
	}

	/**
	 * Returns all the matching stubs according to the regexString.
	 * 
	 * TODO catch any exception that could be raise by the regex search, to avoid such uncaught exceptions.
	 *  
	 * @param regexString
	 * @param nodePath
	 * @param stubInterface
	 * @return
	 */
	public <S> String[] getPublicMatchingStubNames(String regexString, Class<S> stubInterface) {
		List<String> matchingStubNames = new ArrayList<String>(); 
		for (Entry<String,Object> entry : stubMap.entrySet()) {
			if (stubInterface.isAssignableFrom(entry.getValue().getClass()) && entry.getKey().matches(regexString)) {				
				matchingStubNames.add(entry.getKey());
			}
		}	
		
		return matchingStubNames.toArray(new String[]{});
	}

}
