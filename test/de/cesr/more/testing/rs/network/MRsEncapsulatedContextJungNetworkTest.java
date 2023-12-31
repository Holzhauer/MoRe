/**
 * This file is part of
 * 
 * MORe - Managing Ongoing Relationships
 *
 * Copyright (C) 2010 Center for Environmental Systems Research, Kassel, Germany
 * 
 * MORe - Managing Ongoing Relationships is free software: You can redistribute 
 * it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *  
 * MORe - Managing Ongoing Relationships is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Center for Environmental Systems Research, Kassel
 * 
 * Created by Sascha Holzhauer on 15.01.2010
 */
package de.cesr.more.testing.rs.network;


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
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.lara.components.container.properties.impl.LFloatProperty;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.model.impl.LAbstractStandaloneSynchronisedModel;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.impl.LRandomService;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.lara.agent.MLaraNetworkEnvironment;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsEncapsulatedContextJungNetwork;
import de.cesr.more.testing.testutils.MTestUtilsLara.MTestNetworkAgent;



/**
 * 
 * @author Sascha Holzhauer
 * @date 15.01.2010
 * 
 */
public class MRsEncapsulatedContextJungNetworkTest {

	TestAgent														center;
	LaraEnvironment													env;
	MoreNetwork<TestAgent, MRepastEdge<TestAgent>>					network;
	Context<TestAgent> 												context;


	/**
	 * test agent
	 */
	public static class TestAgent extends MTestNetworkAgent<TestAgent> {
		
		static int id = 0;
		/**
		 * constructor
		 * 
		 * @param env
		 * @param value
		 */
		public TestAgent(LaraEnvironment env, float value) {
			super(env, "testAgent_" + id++);
			getLaraComp().getGeneralMemory().memorize(new LFloatProperty("Value", value));
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		LModel.setNewModel(new LAbstractStandaloneSynchronisedModel() {

			@Override
			public LaraRandom getLRandom() {
				return new LRandomService((int) System.currentTimeMillis());
			}

			@Override
			public void createAgents() {
				// TODO Auto-generated method stub

			}

			@Override
			public void finish() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void execute(LaraDecisionConfiguration arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void processStep() {
				// TODO Auto-generated method stub

			}
		});
		env = new MLaraNetworkEnvironment<TestAgent, MRepastEdge<TestAgent>>();
		context = new DefaultContext<TestAgent>();
		network = new MRsEncapsulatedContextJungNetwork<TestAgent, MRepastEdge<TestAgent>>(
				new ContextJungNetwork<TestAgent>(new UndirectedJungNetwork<TestAgent>("network"), context),
				context);

		// build network (star of max diameter 5):
		center = new TestAgent(env, 1000);
		context.add(center);
		network.addNode(center);

		for (int i = 0; i < 5; i++) {
			TestAgent next = new TestAgent(env, 200);
			context.add(next);
			network.addNode(next);
			network.connect(center, next);
			for (int j = 0; j < 5; j++) {
				TestAgent outer = new TestAgent(env, 30);
				context.add(outer);
				network.addNode(outer);
				network.connect(next, outer);
			}
		}
		System.out.println(network.getNodes());
	}

	/**
	 * Test method for {@link de.cesr.more.lara.adapter.snrs.DefaultLRsNetwork#connect(Object, Object)} .
	 */
	@Test
	public final void testConnect() {
		TestAgent target = new TestAgent(env, 1);
		context.add(target);
		network.connect(center, target);
		assertEquals("An edge between both agents should exist", true, network.isAdjacent(center, target));
	}

	/**
	 * Test method for {@link de.cesr.more.lara.adapter.snrs.DefaultLRsNetwork#disconnect(Object, Object)} .
	 */
	@Test
	public final void testDisconnect() {
		TestAgent enemy = network.getAdjacent(center).iterator().next();
		assertEquals("Both agents should be adjacent", true, network.isAdjacent(center, enemy));
		network.disconnect(center, enemy);
		assertEquals("Both agents should not be adjacent anymore", false, network.isAdjacent(center, enemy));
	}

	/**
	 * test of getter
	 */
	@Test
	public final void testGetAdjacent() {
		Collection<TestAgent> adjacent = new ArrayList<TestAgent>();
		Iterator<TestAgent> iter = network.getAdjacent(center).iterator();
		while (iter.hasNext()) {
			adjacent.add(iter.next());
		}
		assertEquals("Number of adjacent nodes should be 5", 5, adjacent.size());
	}

	/**
	 * Test method for {@link de.cesr.more.lara.adapter.snrs.DefaultLRsNetwork#numNodes()}.
	 */
	@Test
	public final void testNumNodes() {
		assertEquals("The network should contain 1+5+5*5 = 31 agents", 31, network.numNodes());
	}

	/**
	 * Test method for {@link de.cesr.more.lara.adapter.snrs.DefaultLRsNetwork#setWeight(Object, Object, double)} .
	 */
	@Test
	public final void testSetWeight() {
		TestAgent friend = network.getAdjacent(center).iterator().next();
		assertEquals("Weight of initialization should be 1.0", MRsEncapsulatedContextJungNetwork.DEFAULT_EDGE_WEIGHT, network
				.getWeight(center, friend), 0);
		network.setWeight(center, friend, 42.0);
		assertEquals("Weight of initialization should be 42.0", 42.0, network.getWeight(center, friend), 0);
	}

	/**
	 * 
	 */
	@Test
	public final void testNormalizeWeights() {
		for (TestAgent next : network.getAdjacent(center)) {
			network.setWeight(center, next, 50);
			for (TestAgent edge : network.getAdjacent(next)) {
				if (edge != center) {
					network.setWeight(next, edge, 5);
				}
			}
		}
		network.normalizeWeights();
		for (TestAgent next : network.getAdjacent(center)) {
			assertEquals("Normalized weight should be 50 / 50 = 1.0", 1.0, network.getWeight(center, next), 0);
			for (TestAgent edge : network.getAdjacent(next)) {
				if (edge != center) {
					assertEquals("Normalized weight should be 5 / 50 = 0.1", 0.1, network.getWeight(next, edge), 0);
				}
			}
		}
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
