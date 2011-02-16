/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 15.01.2010
 */
package de.cesr.more.testing.adapter.snrs;



import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.lara.components.LaraAgent;
import de.cesr.lara.components.LaraAgentComponent;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraDecisionBuilder;
import de.cesr.lara.components.LaraDecisionData;
import de.cesr.lara.components.LaraEnvironment;
import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.LaraRandom;
import de.cesr.lara.components.LaraSimpleAgent;
import de.cesr.lara.components.impl.AbstractLaraAgent;
import de.cesr.lara.components.impl.AbstractStandaloneParallelModel;
import de.cesr.lara.components.impl.LModel;
import de.cesr.lara.components.impl.container.properties.LFloatProperty;
import de.cesr.lara.components.impl.environment.AbstractEnvironmentalProperty;
import de.cesr.lara.components.impl.util.LRandom;
import de.cesr.more.lara.LNetworkEnvironment;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.rs.adapter.DefaultLRsNetwork;
import de.cesr.more.rs.adapter.MRepastEdge;
import de.cesr.more.testing.LNetworkAnalysisTest.TestAgent;



/**
 * 
 * @author Sascha Holzhauer
 * @date 15.01.2010
 * 
 */
public class DefaultRSNetworkTest {

	LaraAgent				center;
	LaraEnvironment			env;
	MoreNetwork<LaraAgent, MRepastEdge<LaraAgent>>	network;

	/**
	 * test agent
	 */
	public static class TestAgent extends AbstractLaraAgent {

		/**
		 * constructor
		 * @param env
		 * @param value
		 */
		public TestAgent(LaraEnvironment env, float value) {
			super(env);
			getMemory().memorize(new LFloatProperty("Value", value));
		}

		@Override
		public void perceive(LaraDecisionBuilder dBuilder) {
		}

		@Override
		public LaraDecisionData getDecisionData(LaraDecisionBuilder dBuilder) {
			return null;
		}
	}

	/**
	 * @throws java.lang.Exception
	 *             Created by Sascha Holzhauer on 15.01.2010
	 */
	@Before
	public void setUp() throws Exception {

		LModel.setNewModel(new AbstractStandaloneParallelModel() {
			
			@Override
			public void finish() {
			}
			
			@Override
			public void createAgents() {
			}

			@Override
			protected void processDecisions(LaraDecisionBuilder dBuilder) {
			}

			@Override
			public LaraRandom getLRandom() {
				return new LRandom((int) System.currentTimeMillis());
			}

			@Override
			public void process() {
			}
		});
		env = new LNetworkEnvironment();
		Context context = new DefaultContext<LaraAgent>();
		network = new DefaultLRsNetwork<LaraAgent, MRepastEdge<LaraAgent>>(new ContextJungNetwork<LaraAgent>(new UndirectedJungNetwork<LaraAgent>("network"),
				context), context);

		// build network (star of max diameter 5):
		center = new TestAgent(env, 1000);

		for (int i = 0; i < 5; i++) {
			LaraAgent next = new TestAgent(env, 200);
			network.connect(center, next);
			for (int j = 0; j < 5; j++) {
				LaraAgent edge = new TestAgent(env, 30);
				network.connect(next, edge);
			}
		}
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.lara.adapter.snrs.DefaultLRsNetwork#connect(Object, Object)}
	 * .
	 */
	@Test
	public final void testConnect() {
		LaraAgent target = new TestAgent(env, 1);
		network.connect(center, target);
		assertEquals("An edge between both agents should exist", true, network.isAdjacent(center, target));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.lara.adapter.snrs.DefaultLRsNetwork#disconnect(Object, Object)}
	 * .
	 */
	@Test
	public final void testDisconnect() {
		LaraAgent enemy = network.getAdjacent(center).iterator().next();
		assertEquals("Both agents should be adjacent", true, network.isAdjacent(center, enemy));
		network.disconnect(center, enemy);
		assertEquals("Both agents should not be adjacent anymore", false, network.isAdjacent(center, enemy));
	}

	/**
	 * test of getter
	 */
	@Test
	public final void testGetAdjacent() {
		Collection<LaraAgent> adjacent = new ArrayList<LaraAgent>();
		Iterator<LaraAgent> iter = network.getAdjacent(center).iterator();
		while (iter.hasNext()) {
			adjacent.add(iter.next());
		}
		assertEquals("Number of adjavend nodes should be 5", 5, adjacent.size());
	}

	/**
	 * Test method for {@link de.cesr.more.lara.adapter.snrs.DefaultLRsNetwork#numNodes()}.
	 */
	@Test
	public final void testNumNodes() {
		assertEquals("The network should contain 1+5+5*5 = 31 agents", 31, network.numNodes());
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.lara.adapter.snrs.DefaultLRsNetwork#setWeight(Object, Object, double)}
	 * .
	 */
	@Test
	public final void testSetWeight() {
		LaraAgent friend = network.getAdjacent(center).iterator().next();
		assertEquals("Weight of initialization should be 1.0", DefaultLRsNetwork.DEFAULT_EDGE_WEIGHT, 
				network.getWeight(center, friend), 0);
		network.setWeight(center, friend, 42.0);
		assertEquals("Weight of initialization should be 42.0", 42.0, network.getWeight(center, friend), 0);
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 18.01.2010
	 */
	@Test
	public final void testNormalizeWeights() {
		for (LaraAgent next : network.getAdjacent(center)) {
			network.setWeight(center, next, 50);
			for (LaraAgent edge : network.getAdjacent(next)) {
				if (edge != center) {
					network.setWeight(next, edge, 5);
				}
			}
		}
		network.normalizeWeights();
		for (LaraAgent next : network.getAdjacent(center)) {
			assertEquals("Normalized weight should be 50 / 50 = 1.0", 1.0, network.getWeight(center, next), 0);
			for (LaraAgent edge : network.getAdjacent(next)) {
				if (edge != center) {
					assertEquals("Normalized weight should be 5 / 50 = 0.1", 0.1, network.getWeight(next, edge), 0);
				}
			}
		}
	}

	/**
	 * @throws java.lang.Exception
	 *             Created by Sascha Holzhauer on 15.01.2010
	 */
	@After
	public void tearDown() throws Exception {
		center = null;
		env = null;
		network = null;
	}
}
