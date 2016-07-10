package com.jelectro.node;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;

import com.jelectro.utils.InstanceCache;

public class NodePath implements Serializable {

	private static final long serialVersionUID = 5998640222551558391L;

	private NodeKey[] keysPath;

	private NodePath() {
	}

	public NodePath(NodeKey... keysPath) {
		this();
		this.keysPath = keysPath;
	}

	public NodePath(NodePath currentPath, NodeKey newDestinationNodeKey) {
		this.keysPath = new NodeKey[currentPath.keysPath.length + 1];
		System.arraycopy(currentPath.keysPath, 0, keysPath, 0, currentPath.keysPath.length);
		keysPath[currentPath.keysPath.length] = newDestinationNodeKey;
	}

	public NodeKey[] getKeysPath() {
		return keysPath;
	}

	public NodeKey getOriginNode() {
		return keysPath[0];
	}

	public NodeKey getDestinationNode() {
		return keysPath[keysPath.length - 1];
	}

	public NodeKey getNextNode(NodeKey currentNodeKey) {
		for (int i = 0; i < keysPath.length - 1; i++) {
			if (keysPath[i].equals(currentNodeKey))
				return keysPath[i + 1];
		}
		return null;
	}

	public NodeKey getPreviousNode(NodeKey currentNodeKey) {
		for (int i = 1; i < keysPath.length; i++) {
			if (keysPath[i].equals(currentNodeKey))
				return keysPath[i - 1];
		}
		return null;
	}

	public NodePath reverse() {
		final NodeKey[] keys = new NodeKey[keysPath.length];
		for (int i = 0; i < keysPath.length; i++) {
			keys[i] = keysPath[keysPath.length - i - 1];
		}
		return new NodePath(keys);

	}

	/**
	 * Just need to be commented !!!
	 * @return
	 * @throws ObjectStreamException
	 */
	Object writeReplace() throws ObjectStreamException {
		return InstanceCache.getRegisteredSingleton(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(keysPath);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodePath other = (NodePath) obj;
		if (!Arrays.equals(keysPath, other.keysPath))
			return false;
		return true;
	}

}
