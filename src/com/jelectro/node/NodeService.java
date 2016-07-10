package com.jelectro.node;

import java.util.Random;

/**
 * 
 * 
 * 
 * @author xneb
 * 
 */
public class NodeService {

	private static final int KEY_BYTE_LENGTH = 5;

	public NodeKey createNodeKey(String nodeName) {

		return new NodeKey(nodeName, getId());

	}

	protected byte[] getId() {
		Random r = new Random(System.nanoTime());
		byte[] b = new byte[KEY_BYTE_LENGTH];
		r.nextBytes(b);
		return b;
	}

			
	
}
