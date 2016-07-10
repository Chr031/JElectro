package com.jelectro.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodePathList {

	private final List<NodePath> nodePathList;
	
	protected NodePathList() {
		nodePathList = new ArrayList<NodePath>();
	}
	
	public NodePathList(NodePath ...pathes ) {
		this();
		addNodePath(pathes);
	}
	
	public NodePath getMainPath() {
		return nodePathList.get(0);
	}
	
	public void addNodePath(NodePath ...pathes ) {
		nodePathList.addAll(Arrays.asList(pathes));
	}

	public void removeNodePath(NodePath path) {
		nodePathList.remove(path);
		
	}

	public NodePath[] getPathes() {
		return nodePathList.toArray(new NodePath[]{});
	}
	
	
}
