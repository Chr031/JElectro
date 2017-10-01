package com.jelectro.stubs;

import com.jelectro.node.Node;
import com.jelectro.node.NodeKey;
import com.jelectro.node.NodePath;
import com.jelectro.node.NodePathList;
import com.jelectro.stubs.StubProxyFactory.StubProxyHandler;

public class StubReference<S> {

	private final Node node;
	private final String stubName;
	private final Class<S> stubInterface;
	private final NodeKey locationNodeKey;
	private final NodePathList nodePaths;
	
	
	private StubProxyHandler<S> stubProxyHandler;
	private final Object lock = new Object(); 
	
	
	public StubReference(Node node, String stubName, Class<S> stubInterface, NodePath stubNodePath) {
		this.node = node;
		this.stubName = stubName;
		this.stubInterface = stubInterface;
		this.locationNodeKey = stubNodePath.getDestinationNode();
		nodePaths = new NodePathList();
		nodePaths.addNodePath(stubNodePath);
		
		
		
	}

	public String getStubName() {
		return stubName;
	}

	public Class<S> getStubInterface() {
		return stubInterface;
	}

	public NodeKey getLocationNodeKey() {
		return locationNodeKey;
	}

	/**
	 * Returns the stub proxy associated to this reference. 
	 * @return
	 */
	public S getStubProxy() {
		if (stubProxyHandler==null) {
			synchronized (lock) {
				if (stubProxyHandler == null) {
					stubProxyHandler = StubProxyFactory.createStubProxyHandler(node, stubName, stubInterface, nodePaths);
				}
			}
		}
		return stubProxyHandler.getStub();
		
	}

	/**
	 * <p>
	 * Returns the path to reach the node where the stub instance lies. This
	 * path should be a full path starting from the current node key of the
	 * reference node owner and ending with the locationNodeKey.
	 * </p>
	 * <p>
	 * Having more than one path means that the stub is reachable from multiple
	 * ways.
	 * </p>
	 * 
	 * @return
	 */
	public NodePath[] getPaths() {
		return nodePaths.getPathes();
	}

	public void addPath(NodePath... path) {
		nodePaths.addNodePath(path);
	}
	
	public void removePath(NodePath path) {
		nodePaths.removeNodePath(path);
	}
	
	@Deprecated
	public  boolean matches(String stubName, Class<S> stubInterface) {
		if (this.stubName == null) {
			if (stubName != null)
				return false;
		} else if (!this.stubName.equals(stubName))
			return false;
		if (this.stubInterface == null) {
			if (stubInterface != null)
				return false;
		} else if (!this.stubInterface.equals(stubInterface))
			return false;
		return true;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locationNodeKey == null) ? 0 : locationNodeKey.hashCode());
		result = prime * result + ((stubName == null) ? 0 : stubName.hashCode());
		result = prime * result + ((stubInterface == null) ? 0 : stubInterface.hashCode());
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
		StubReference other = (StubReference) obj;
		if (locationNodeKey == null) {
			if (other.locationNodeKey != null)
				return false;
		} else if (!locationNodeKey.equals(other.locationNodeKey))
			return false;
		if (stubName == null) {
			if (other.stubName != null)
				return false;
		} else if (!stubName.equals(other.stubName))
			return false;
		if (stubInterface == null) {
			if (other.stubInterface != null)
				return false;
		} else if (!stubInterface.equals(other.stubInterface))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StubReference [stubName=" + stubName + ", stubInterface=" + stubInterface + ", locationNodeKey=" + locationNodeKey + "]";
	}

}