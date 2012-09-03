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
 * Created by Sascha Holzhauer on 14.05.2012
 */
package de.cesr.more.testing.rs.building;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.DefaultContext;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MRsCompleteNetworkBuilder;
import de.cesr.more.rs.building.edge.MRsEdgeFactory;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 14.05.2012
 * 
 */
public class MRsCompleteNetworkBuilderTest {

	static final int	NUM_AGENTS	= 20;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link de.cesr.more.rs.building.MRsCompleteNetworkBuilder#buildNetwork(java.util.Collection)}.
	 */
	@Test
	public void testBuildNetwork() {
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, true);

		List<MTestNode> agents = new ArrayList<MTestNode>(NUM_AGENTS);
		for (int i = 0; i < NUM_AGENTS; i++) {
			agents.add(new MTestNode());
		}

		MRsCompleteNetworkBuilder<MTestNode, MRepastEdge<MTestNode>> netService =
				new MRsCompleteNetworkBuilder<MTestNode, MRepastEdge<MTestNode>>(
						new MRsEdgeFactory<MTestNode, MRepastEdge<MTestNode>>());
		netService.setContext(new DefaultContext<MTestNode>());

		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, true);
		MoreNetwork<MTestNode, MRepastEdge<MTestNode>> net = netService.buildNetwork(agents);

		assertEquals(20, net.numNodes());
		assertEquals(20 * (20 - 1), net.numEdges());

		List<MTestNode> predecessors = new ArrayList<MTestNode>();
		for (MTestNode node : net.getPredecessors(agents.get(2))) {
			predecessors.add(node);
		}
		assertEquals((20 - 1), predecessors.size());

		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, false);
		net = netService.buildNetwork(agents);

		assertEquals(20, net.numNodes());
		assertEquals(20 * (20 - 1) / 2, net.numEdges());

		predecessors = new ArrayList<MTestNode>();
		for (MTestNode node : net.getPredecessors(agents.get(2))) {
			predecessors.add(node);
		}
		assertEquals((20 - 1), predecessors.size());
	}

	/**
	 * Test method for {@link de.cesr.more.rs.building.MGeoRsCompleteNetworkBuilder#buildNetwork(java.util.Collection)}.
	 */
	@Test
	public void testAddAndLinkNode() {
		List<MTestNode> agents = new ArrayList<MTestNode>(NUM_AGENTS);
		for (int i = 0; i < NUM_AGENTS; i++) {
			agents.add(new MTestNode());
		}

		MRsCompleteNetworkBuilder<MTestNode, MRepastEdge<MTestNode>> netService =
				new MRsCompleteNetworkBuilder<MTestNode, MRepastEdge<MTestNode>>(
						new MRsEdgeFactory<MTestNode, MRepastEdge<MTestNode>>());
		netService.setContext(new DefaultContext<MTestNode>());

		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, true);
		MoreNetwork<MTestNode, MRepastEdge<MTestNode>> net = netService.buildNetwork(agents);

		assertEquals(20, net.numNodes());
		assertEquals(20 * (20 - 1), net.numEdges());

		netService.addAndLinkNode(net, new MTestNode());
		assertEquals(21, net.numNodes());
		assertEquals(21 * (21 - 1), net.numEdges());

		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, false);
		net = netService.buildNetwork(agents);

		assertEquals(20, net.numNodes());
		assertEquals(20 * (20 - 1) / 2, net.numEdges());

		netService.addAndLinkNode(net, new MTestNode());
		assertEquals(21, net.numNodes());
		assertEquals(21 * (21 - 1) / 2, net.numEdges());
	}

}
