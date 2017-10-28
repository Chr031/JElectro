package com.jelectro.stubs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.jelectro.JElectro;
import com.jelectro.utils.FireListeners;

public class FutureStubSet<S> implements StubSet<S>, IElementProducerListener<StubReference<S>> {

	private static final Logger log = Logger.getLogger(FutureStubSet.class);
	
	private final String regexLookupString;
	private final Class<S> stubInterface;
	
	private final List<StubReference<S>> stubReferenceList;
	private final Object lock;
	private final FireListeners<StubSetPathListener<S>> stubSetListeners;
	private final ArrayList<LookupResultStubProducer<S>> elementProducerList;

	
	private final class StubSetListenerDecorator<St> implements StubSetPathListener<St> {		
		
		private final StubSetListener<St> decorated;
		
		
		public StubSetListenerDecorator(StubSetListener<St> stubSetListener) {
			super();
			this.decorated = stubSetListener;
		}

		@Override
		public void onStubReceived(St stub) {
			decorated.onStubReceived(stub);
			
		}

		@Override
		public void onStubPathUpdated(St stub) {
			if (decorated instanceof StubSetPathListener)
				((StubSetPathListener<St>) decorated).onStubPathUpdated(stub);
			
		}
		
	}
	
	
	public FutureStubSet(String regexLookupString, Class<S> stubInterface) {
		this.stubReferenceList = new CopyOnWriteArrayList<>();
		this.lock = new Object();
		this.stubSetListeners = new FireListeners<>();
		this.regexLookupString = regexLookupString;
		this.stubInterface = stubInterface;	
		elementProducerList = new ArrayList<>();
		
	}

	@Override
	public S getLoadBalancedStatelessStub() {
		// TODO To be implemented and commented
		return null;
	}

	@Override
	public S getLoadBalancedStatefullStub() {
		// TODO To be implemented and commented
		return null;
	}

	@Override
	public Iterator<S> iterator() {
		final List<S> tmpList;
		synchronized (lock) {
			tmpList = new ArrayList<S>(stubReferenceList.size());
			for (StubReference<S> stubRef : stubReferenceList) {
				tmpList.add(stubRef.getStubProxy());				
			}
		}
		return tmpList.iterator();
	}

	/**
	 * Method that blocks until at least i stubs are present in this instance.
	 * 
	 * @param i
	 */
	@Override
	public boolean waitFor(int i) {
		int count = 0, maxCount = 100; 
		if (stubReferenceList.size() < i) {
			synchronized (lock) {
				try {
					while (stubReferenceList.size() < i && ++count < maxCount) {
						lock.wait(JElectro.DEFAULT_STUB_TIMEOUT/maxCount);
					}
				} catch (InterruptedException e) {
					// can be ignored !0!
					log.debug("Waitfor interrupted", e);
				}
				return stubReferenceList.size()>=i;
			}
		}
		return true;
	}

	@Override
	public void onElementProduced(StubReference<S> stubReference) {
		synchronized (lock) {

			int stubReferenceIndex = stubReferenceList.indexOf(stubReference);
			if (stubReferenceIndex == -1) {
				stubReferenceList.add(stubReference);
				if (stubSetListeners.isFireProxyReady())
					stubSetListeners.getFireProxy().onStubReceived(stubReference.getStubProxy());
				log.debug("StubReference added");

			} else {
				stubReferenceList.get(stubReferenceIndex).addPath(stubReference.getPaths());
				if (stubSetListeners.isFireProxyReady())
					stubSetListeners.getFireProxy().onStubPathUpdated(stubReferenceList.get(stubReferenceIndex).getStubProxy());
				
				log.debug("New stub path added");
			}

			lock.notifyAll();

		}
	}

	
	@Override
	public int size() {
		return stubReferenceList.size();
	}

	@Override
	public S get(int i) {
		boolean wait1 = waitFor(i + 1);	
		if (!wait1) 
			log.debug("Stubs are not all present ???");
		return stubReferenceList.get(i).getStubProxy();
	}

	
	public void addStubSetListeners(StubSetListener<S>... listeners) {
		
		Arrays.stream(listeners).map(StubSetListenerDecorator<S>::new).forEach(stubSetListeners::addListener);
		log.debug("StubSetListener added");

	}

	public void registerStubProducer(LookupResultStubProducer<S> stubProducer) {
		stubProducer.addElementProducerListener(this);
		// we keep a track of the stubProducer so that it is not garbage collected, since there is a chain of weak listeners 
		elementProducerList.add(stubProducer);
		
	}

}
