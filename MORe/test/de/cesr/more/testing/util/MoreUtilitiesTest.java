package de.cesr.more.testing.util;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.collections15.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.edges.MoreEdge;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.testing.MTestGraphs;
import de.cesr.more.testing.MTestGraphs.TestNode;
import de.cesr.more.util.MoreUtilities;

public class MoreUtilitiesTest {

	MoreNetwork<TestNode, MoreEdge<TestNode>> network;
	private final String OUT_DIR = "test/res/outnet.graphml";
	
	@Before
	public void setUp() throws Exception {
		network = MTestGraphs.getCompleteDirectedMNetwork(5);
	}

	@After
	public void tearDown() throws Exception {
		network = null;
	}

	@Test
	public final void testOutputGraph() {
		MoreUtilities.outputGraph(network, new File(OUT_DIR));
		MoreNetwork<TestNode, MoreEdge<TestNode>> in_net = MoreUtilities.inputNetwork(new File(OUT_DIR),
				new Factory<TestNode>() {

					@Override
					public TestNode create() {
						return new TestNode();
					}
			
		}, "NewNet");
		assertEquals("Number of nodes", in_net.numNodes(), network.numNodes());
		assertEquals("Number of edges", in_net.numEdges(), network.numEdges());
	}
}
