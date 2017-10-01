package com.jelectro.stubs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.blue.tools.utils.WeakFireListeners;

import com.jelectro.JElectro;

import tools.logger.Logger;

public class FutureStubSet<S> implements StubSet<S>, IElementProducerListener<StubReference<S>> {

	private static final Logger log = Logger.getLogger(FutureStubSet.class);

	private final IElementProducer<StubReference<S>> stubProducer;
	private final List<StubReference<S>> stubReferenceList;
	private final Object lock;
	private final WeakFireListeners<StubSetListener<S>> stubSetListeners;

	public FutureStubSet(IElementProducer<StubReference<S>> stubProducer) {
		this.stubProducer = stubProducer;
		this.stubReferenceList = new ArrayList<StubReference<S>>();
		this.lock = new Object();
		stubSetListeners = new WeakFireListeners<>();
		
		stubProducer.addElementProducerListener(this);
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
	public void waitFor(int i) {
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
			}
		}
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

	public IElementProducer<StubReference<S>> getStubProducer() {
		return stubProducer;
	}

	@Override
	public int size() {
		return stubReferenceList.size();
	}

	@Override
	public S get(int i) {
		waitFor(i + 1);
		return stubReferenceList.get(i).getStubProxy();
	}

	
	public void addStubSetListener(StubSetListener<S>... listeners) {
		
		stubSetListeners.addListeners(listeners);
		log.debug("StubSetListener added");

	}

}
