package com.jelectro.stubs;


/**
 * Generic producer interface
 * @author Bleu
 *
 * @param <S>
 */
public interface IElementProducer<S> {
	
	
	public void addElement(S stub);
	
	public void addElementProducerListener(IElementProducerListener<S> epl);
	
		
}
