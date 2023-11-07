package de.cesr.more.testing.util;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.collections15.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.testing.testutils.MTestGraphs;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;
import de.cesr.more.util.io.MoreIoUtilities;

public class MoreUtilitiesTest {

	MoreNetwork<MTestNode, MoreEdge<MTestNode>> network;
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
		MoreIoUtilities.outputGraph(network, new File(OUT_DIR));
		MoreNetwork<MTestNode, MoreEdge<MTestNode>> in_net = MoreIoUtilities.inputNetwork(new File(OUT_DIR),
				new Factory<MTestNode>() {

					@Override
					public MTestNode create() {
						return new MTestNode();
					}
			
		}, "NewNet");
		assertEquals("Number of nodes", in_net.numNodes(), network.numNodes());
		assertEquals("Number of edges", in_net.numEdges(), network.numEdges());
	}
}
