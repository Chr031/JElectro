package com.jelectro.stubs;

/**
 * TODO this interface needs some comments and explanations
 * @author Christophe
 *
 * @param <S>
 */
public interface StubSet<S> extends Iterable<S> {

	public abstract S getLoadBalancedStatelessStub() ;
	
	public abstract S getLoadBalancedStatefullStub() ;

	public abstract S get(int i);

	public abstract int size();

	public abstract boolean waitFor(int i);
	
	abstract void addStubSetListeners(StubSetListener<S>... listeners);
}
