package com.jelectro.stubs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.jelectro.exception.JElectroException;
import com.jelectro.node.NodeKey;
import com.jelectro.node.NodePath;

import tools.logger.Logger;

/**
 * This class is intended to reflect any published stub name address and kind.
 * Once a stub is exported by a remote instance, a reference to this stub with
 * the path to access it should be present in this class.
 * 
 * This class is quite complicated because it has to be thread safe. It will be
 * updated simultaneously when running.
 * 
 * @author xneb
 * 
 */
@Deprecated
public class StubBus {
	
	private static final Logger log = Logger.getLogger(StubBus.class);

	private final Object lockSet;

	private Map<StubReference<?>, StubReference<?>> stubMap;

	private final List<StubBusListener> listeners;

	public StubBus() {
		stubMap = new ConcurrentHashMap<StubReference<?>, StubReference<?>>();
		lockSet = new Object();
		listeners = new ArrayList<StubBusListener>();
	}

	public void addListener(StubBusListener l) {
		listeners.add(l);
	}

	public void removeListener(StubBusListener l) {
		listeners.remove(l);
	}
	
	public Set<StubReference<?>> stubSet() {
		return stubMap.keySet();
	}
	
	
	public <S> List<StubReference<S>> getStub(String stubName,Class<S> stubInterface) {
		List<StubReference<S>> refs = new ArrayList<StubReference<S>>();
		for (StubReference<?> uRef : stubMap.keySet()) {
			@SuppressWarnings("unchecked")
			final StubReference<S> ref = (StubReference<S>) uRef;
			if (ref.matches (stubName, stubInterface))
				refs.add(ref);
		}
		return refs;
	}

	/**
	 * <p>
	 * Add a new instance of stub reference in the bus
	 * </p>
	 * <p>
	 * Note that the networkNodePath has to be complete : this means that it
	 * must start with the current local {@link NodeKey} and should end with the
	 * instance location {@link NodeKey}
	 * </p>
	 * 
	 * 
	 * @param name
	 *            the name of the stub
	 * @param stubInterface
	 *            the interface of the stub
	 * @param locationNodeKey
	 *            the node where the instance is present
	 * @param networkNodePath
	 *            the path to reach the instance
	 * @throws JElectroException 
	 */
	public <S> void addStub(String name, Class<S> stubInterface, NodeKey locationNodeKey, NodePath networkNodePath) throws JElectroException {
		StubReference<S> reference = new StubReference<S>(name, stubInterface, locationNodeKey);

		//if (!networkNodePath.getDestinationNode().equals(locationNodeKey)) throw new JElectroException("Path and location are not coherent ");
		
		synchronized (lockSet) {

			if (!stubMap.containsKey(reference)) {
				reference.addPath(networkNodePath);
				stubMap.put(reference, reference);
				onStubAdded(reference);
				log.debug("Stub added " + name + " " + locationNodeKey);
				return;
			}
				
			

			// at this point stub is already present :
			// we have to add the new networkNodePath but this will increase the
			// network traffic ...
			@SuppressWarnings("unchecked")
			StubReference<S> localReference = ( StubReference<S>)stubMap.get(reference);
			localReference.addPath(networkNodePath);
			log.debug("Stub path updated : " + name + " " + locationNodeKey);

		}
	}
	
	
	

	private <S> void onStubAdded(StubReference<S> reference) {
		for (StubBusListener l : listeners) {
			l.onStubAdded(reference);
		}
	}

	public interface StubBusListener {

		<S> void onStubAdded(StubReference<S> reference);

		<S> void onStubRemoved(StubReference<S> reference);

	}

}
