package com.jelectro.node;

import org.junit.Assert;

import org.junit.Test;

import com.jelectro.node.NodeKey;
import com.jelectro.node.NodePath;

public class NodePathTest {

	@Test
	public void testNodePath() {
		NodeKey nk1 = new NodeKey("1", new byte[] { 1, 1, 1 });
		NodeKey nk2 = new NodeKey("2", new byte[] { 1, 1, 2 });
		NodeKey nk3 = new NodeKey("3", new byte[] { 1, 1, 3 });
		NodeKey nk4 = new NodeKey("4", new byte[] { 1, 1, 4 });

		NodePath np = new NodePath(nk1, nk2, nk3, nk4);

		Assert.assertEquals("Destination node is incorrect", nk4, np.getDestinationNode());
		Assert.assertEquals("Original node is incorrect", nk1, np.getOriginNode());

		Assert.assertEquals(nk2, np.getNextNode(nk1));
		Assert.assertEquals(nk3, np.getNextNode(nk2));
		Assert.assertEquals(nk4, np.getNextNode(nk3));

		Assert.assertEquals(nk1, np.getPreviousNode(nk2));
		Assert.assertEquals(nk2, np.getPreviousNode(nk3));
		Assert.assertEquals(nk3, np.getPreviousNode(nk4));

	}

	@Test
	public void testNodePathReverse() {

		NodeKey nk1 = new NodeKey("1", new byte[] { 1, 1, 1 });
		NodeKey nk2 = new NodeKey("2", new byte[] { 1, 1, 2 });
		NodeKey nk3 = new NodeKey("3", new byte[] { 1, 1, 3 });
		NodeKey nk4 = new NodeKey("4", new byte[] { 1, 1, 4 });

		NodePath np = new NodePath(nk1, nk2, nk3, nk4);

		Assert.assertEquals("Destination node is incorrect", nk4, np.getDestinationNode());
		Assert.assertEquals("Original node is incorrect", nk1, np.getOriginNode());

		NodePath reverseNp = np.reverse();

		Assert.assertEquals("Reverse destination node is incorrect", nk1, reverseNp.getDestinationNode());
		Assert.assertEquals("Reverse original node is incorrect", nk4, reverseNp.getOriginNode());

		Assert.assertEquals(nk3, reverseNp.getNextNode(nk4));
		Assert.assertEquals(nk2, reverseNp.getNextNode(nk3));
		Assert.assertEquals(nk1, reverseNp.getNextNode(nk2));

	}

}
