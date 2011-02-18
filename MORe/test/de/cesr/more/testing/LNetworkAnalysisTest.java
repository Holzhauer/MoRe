/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.testing;



import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.lara.components.LaraSimpleAgent;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraDecisionBuilder;
import de.cesr.lara.components.LaraEnvironment;
import de.cesr.lara.components.impl.LModel;
import de.cesr.lara.components.impl.container.properties.LFloatProperty;
import de.cesr.lara.components.impl.container.properties.LIntProperty;
import de.cesr.more.lara.AbstractLCompoundNetworkInfo;
import de.cesr.more.lara.AbstractLaraNetworkAgent;
import de.cesr.more.lara.ComboundNetworkInfo;
import de.cesr.more.lara.LNetworkEnvironment;
import de.cesr.more.lara.LaraSimpleNetworkAgent;
import de.cesr.more.lara.util.LNetworkAnalysis;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.rs.adapter.DefaultLRsNetwork;
import de.cesr.more.rs.adapter.MRepastEdge;


/**
 * TODO convert to More
 * @author Sascha Holzhauer
 * @date 19.01.2010
 * 
 */
public class LNetworkAnalysisTest {

	TestAgent				center;
	LaraEnvironment						env;
	MoreNetwork<TestAgent, MRepastEdge<TestAgent>>	network;

	/**
	 * test agent
	 */
	public static class TestAgent extends AbstractLaraNetworkAgent<TestAgent, MRepastEdge<TestAgent>, LaraBehaviouralOption> {

		/**
		 * constructor
		 * 
		 * @param env
		 * @param value
		 * @param name
		 */
		public TestAgent(LaraEnvironment env, float value, String name) {
			super(env, name);
			getLaraComp().getMemory().memorize(new LFloatProperty("Value", value));
		}

		@Override
		public void perceive(LaraDecisionBuilder dBuilder) {
		}
	}

	/**
	 * @throws java.lang.Exception
	 *             Created by Sascha Holzhauer on 15.01.2010
	 */
	@Before
	public void setUp() throws Exception {
		// re-init the LaraModel:
		// TODO: adapt to new LModel
		//LModel.getNewModel();

		env = new LNetworkEnvironment();
		Context context = new DefaultContext<TestAgent>();
		network = new DefaultLRsNetwork<TestAgent,MRepastEdge<TestAgent>>(new ContextJungNetwork<TestAgent>(
				new UndirectedJungNetwork<TestAgent>("network"), context), context);

		// build network (star of max diameter 5):
		center = new TestAgent(env, 1000.0f, "center");

		for (int i = 0; i < 5; i++) {
			TestAgent next = new TestAgent(env, 200.0f, "next" + i);
			network.connect(center, next);
			for (int j = 0; j < 5; j++) {
				TestAgent edge = new TestAgent(env, 30.0f, "edge" + ((i+1)*10)+(j+1));
				network.connect(next, edge);
			}
		}
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 20.01.2010
	 */
	@Test
	public final void testNetworkSize() {
		assertEquals("Num of nodes should be 31", 31, network.numNodes());
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.lara.util.LNetworkAnalysis#getNumReachedNodes(LaraNetwork, de.cesr.lara.components.LaraSimpleAgent, de.cesr.lara.components.LaraSimpleAgent, int, int)}
	 * .
	 */
	@Test
	public final void testGetNumReachedNodes() {
		assertEquals("The number of reached nodes should be 5 + 5*5 = 30", 30, LNetworkAnalysis.getNumReachedNodes(
				network, center, null, 2, 1));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.lara.util.LNetworkAnalysis#getCompoundValue(de.cesr.lara.components.LaraNetwork, de.cesr.more.lara.LaraSimpleNetworkAgent, de.cesr.more.lara.ComboundNetworkInfo)}
	 * .
	 */
	@Test
	public final void testGetCompoundValue() {
		// // TODO test more complicated network structures!
		ComboundNetworkInfo netInfo = new AbstractLCompoundNetworkInfo("Value", 1);
		netInfo = LNetworkAnalysis.<TestAgent, MRepastEdge<TestAgent>> getCompoundValue(network, center, netInfo);
		assertEquals("Reach 1: 5*200*1/5 = 200.0 ", 200.0, netInfo.getValue(), 0);

		netInfo = new AbstractLCompoundNetworkInfo("Value", 2);
		netInfo = LNetworkAnalysis.<TestAgent, MRepastEdge<TestAgent>> getCompoundValue(network, center, netInfo);
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
		netInfo = new AbstractLCompoundNetworkInfo("Value", 1);
		netInfo = LNetworkAnalysis.<TestAgent, MRepastEdge<TestAgent>> getCompoundValue(network, center, netInfo);
		assertEquals("Reach 1: 5*200*1/5 = 200.0 ", 200.0, netInfo.getValue(), 0);

		netInfo = new AbstractLCompoundNetworkInfo("Value", 2);
		netInfo = LNetworkAnalysis.<TestAgent, MRepastEdge<TestAgent>> getCompoundValue(network, center, netInfo);
		assertEquals("Reach 2: (5*200*1 + 5*5*30*1*0.1)/(5+5*5) = 35.83 ", 35.83, netInfo.getValue(), 0.1);
	}

	/**
	 * @throws java.lang.Exception
	 *             Created by Sascha Holzhauer on 19.01.2010
	 */
	@After
	public void tearDown() throws Exception {
		center = null;
		env = null;
		network = null;
	}
}
