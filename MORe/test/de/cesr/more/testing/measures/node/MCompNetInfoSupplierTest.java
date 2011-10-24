/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.testing.measures.node;



import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.node.MCompoundNetworkInfo;
import de.cesr.more.measures.node.MoreComboundNetworkInfo;
import de.cesr.more.measures.node.MoreValueProvidingAgent;
import de.cesr.more.measures.node.supply.MCompNetInfoSupplier;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsEncapsulatedContextJungNetwork;


/**
 * @author Sascha Holzhauer
 * @date 19.01.2010
 * 
 */
public class MCompNetInfoSupplierTest {

	TestAgent										center;
	LaraEnvironment									env;
	MoreNetwork<TestAgent, MRepastEdge<TestAgent>>	network;

	/**
	 * test agent
	 */
	public static class TestAgent implements MoreValueProvidingAgent {
		
		double value;
		String name;

		/**
		 * @param env
		 * @param value
		 * @param name
		 */
		public TestAgent(float value, String name) {
			this.value = value;
			this.name = name;
		}

		/**
		 * @see de.cesr.more.measures.node.MoreValueProvidingAgent#getValue(java.lang.String)
		 */
		@Override
		public double getValue(String key) {
			return value;
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Context<TestAgent> context = new DefaultContext<TestAgent>();
		network = new MRsEncapsulatedContextJungNetwork<TestAgent,MRepastEdge<TestAgent>>(new ContextJungNetwork<TestAgent>(
				new UndirectedJungNetwork<TestAgent>("network"), context), context);

		// build network (star of max diameter 5):
		center = new TestAgent(1000.0f, "center");

		for (int i = 0; i < 5; i++) {
			TestAgent next = new TestAgent(200.0f, "next" + i);
			network.connect(center, next);
			for (int j = 0; j < 5; j++) {
				TestAgent edge = new TestAgent(30.0f, "edge" + ((i+1)*10)+(j+1));
				network.connect(next, edge);
			}
		}
	}

	/**
	 * Test created network
	 */
	@Test
	public final void testNetworkSize() {
		assertEquals("Num of nodes should be 31", 31, network.numNodes());
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.node.supply.MCompNetInfoSupplier#getNumReachedNodes(LaraNetwork, de.cesr.lara.components.agents.LaraAgent, de.cesr.lara.components.agents.LaraAgent, int, int)}
	 * .
	 */
	@Test
	public final void testGetNumReachedNodes() {
		assertEquals("The number of reached nodes should be 5 + 5*5 = 30", 30, MCompNetInfoSupplier.getNumReachedNodes(
				network, center, null, 2, 1));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.node.supply.MCompNetInfoSupplier#getCompoundValue(de.cesr.lara.components.LaraNetwork, de.cesr.more.lara.agent.LaraSimpleNetworkAgent, de.cesr.more.measures.node.MoreComboundNetworkInfo)}
	 * .
	 */
	@Test
	public final void testGetCompoundValue() {
		// TODO test more complicated network structures!
		MoreComboundNetworkInfo netInfo = new MCompoundNetworkInfo("Value", 1);
		netInfo = MCompNetInfoSupplier.<TestAgent, MRepastEdge<TestAgent>> getCompoundValue(network, center, netInfo);
		assertEquals("Reach 1: 5*200*1/5 = 200.0 ", 200.0, netInfo.getValue(), 0);

		netInfo = new MCompoundNetworkInfo("Value", 2);
		netInfo = MCompNetInfoSupplier.<TestAgent, MRepastEdge<TestAgent>> getCompoundValue(network, center, netInfo);
		assertEquals("Reach 2: (5*200*1 + 5*5*30*1*1)/(5*5+5) = 230.0 ", 58.3, netInfo.getValue(), 0.1);

		// test for different edge weights:
		for (TestAgent next : network.getAdjacent(center)) {
			network.setWeight(center, next, 50);
			for (TestAgent edge : network.getAdjacent(next)) {
				if (edge != center) {
					network.setWeight(next, edge, 5);
				}
			}
		}
		network.normalizeWeights();
		netInfo = new MCompoundNetworkInfo("Value", 1);
		netInfo = MCompNetInfoSupplier.<TestAgent, MRepastEdge<TestAgent>> getCompoundValue(network, center, netInfo);
		assertEquals("Reach 1: 5*200*1/5 = 200.0 ", 200.0, netInfo.getValue(), 0);

		netInfo = new MCompoundNetworkInfo("Value", 2);
		netInfo = MCompNetInfoSupplier.<TestAgent, MRepastEdge<TestAgent>> getCompoundValue(network, center, netInfo);
		assertEquals("Reach 2: (5*200*1 + 5*5*30*1*0.1)/(5+5*5) = 35.83 ", 35.83, netInfo.getValue(), 0.01);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		center = null;
		env = null;
		network = null;
	}
}
