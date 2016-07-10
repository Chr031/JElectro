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

	public abstract void waitFor(int i);
	
	abstract void addStubSetListener(IStubSetListener<S>... listeners);
	
	public interface IStubSetListener<St> {
		void onStubReceived(St stub);
		void onStubPathUpdated(St stub);
	}
}
