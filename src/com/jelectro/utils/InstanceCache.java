package com.jelectro.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InstanceCache {

	private final static ConcurrentMap<Object, Object> instanceMap = new ConcurrentHashMap<Object, Object>();

	/**
	 * Saves an instance and return it if another matching equals and hashCode methods is presented.
	 * 
	 * @param instance
	 * @return
	 */
	public static <I> I getRegisteredSingleton(I instance) {
		final I storedInstance = (I) instanceMap.putIfAbsent(instance, instance);
		if (storedInstance == null)
			return instance;
		return storedInstance;
	}
	
	public static void clear(){
		instanceMap.clear();
		
	}
	

}
