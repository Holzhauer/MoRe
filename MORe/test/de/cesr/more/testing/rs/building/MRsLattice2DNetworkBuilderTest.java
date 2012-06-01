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
 * Created by Sascha Holzhauer on 01.06.2012
 */
package de.cesr.more.testing.rs.building;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.DefaultContext;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.param.MNetBuildLattice2DPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MRsCompleteNetworkBuilder;
import de.cesr.more.rs.building.MRsLattice2DNetworkBuilder;
import de.cesr.more.rs.building.edge.MRsEdgeFactory;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;
import de.cesr.more.util.io.MGraphMlWriter;
import de.cesr.more.util.io.MoreIoUtilities;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 01.06.2012 
 *
 */
public class MRsLattice2DNetworkBuilderTest {
	
	// must be >=9
	static final int	NUM_AGENTS	= 16;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link de.cesr.more.rs.building.MRsLattice2DNetworkBuilder#buildNetwork(java.util.Collection)}.
	 */
	@Test
	public void testBuildNetworkCollectionOfAgentType() {
		// undirected, non toroidal:
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, false);
		PmParameterManager.setParameter(MNetBuildLattice2DPa.TOROIDAL, false);
		List<MTestNode> agents = new ArrayList<MTestNode>(NUM_AGENTS);
		for (int i = 0; i < NUM_AGENTS; i++) {
			agents.add(new MTestNode());
		}
		
		MRsLattice2DNetworkBuilder<MTestNode, MRepastEdge<MTestNode>> netService =
				new MRsLattice2DNetworkBuilder<MTestNode, MRepastEdge<MTestNode>>(
						new MRsEdgeFactory<MTestNode, MRepastEdge<MTestNode>>(), "TestNetwork");
		netService.setContext(new DefaultContext<MTestNode>());
		
		MoreNetwork<MTestNode, MRepastEdge<MTestNode>> net = netService.buildNetwork(agents);

		assertEquals(NUM_AGENTS, net.numNodes());
		assertEquals((NUM_AGENTS - (int)Math.sqrt(NUM_AGENTS))*2, net.numEdges());

		int[] edgeDistribution = new int[agents.size() + 1];
		for (MTestNode node : agents) {
			edgeDistribution[net.getDegree(node)]++;
		}
		assertEquals(4, edgeDistribution[2]);
		assertEquals(((int)Math.sqrt(NUM_AGENTS)-2)*4, edgeDistribution[3]);
		assertEquals(NUM_AGENTS - ((int)Math.sqrt(NUM_AGENTS)-2)*4 - 4, edgeDistribution[4]);

		// undirected, toroidal:
		PmParameterManager.setParameter(MNetBuildLattice2DPa.TOROIDAL, true);
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, false);
		netService =
				new MRsLattice2DNetworkBuilder<MTestNode, MRepastEdge<MTestNode>>(
						new MRsEdgeFactory<MTestNode, MRepastEdge<MTestNode>>(), "TestNetwork");
		netService.setContext(new DefaultContext<MTestNode>());
		
		
		net = netService.buildNetwork(agents);

		assertEquals(NUM_AGENTS, net.numNodes());
		assertEquals((NUM_AGENTS)*2, net.numEdges());

		edgeDistribution = new int[agents.size() + 1];
		for (MTestNode node : agents) {
			edgeDistribution[net.getDegree(node)]++;
		}
		assertEquals(NUM_AGENTS, edgeDistribution[4]);
		
		// directed, non toroidal
		PmParameterManager.setParameter(MNetBuildLattice2DPa.TOROIDAL, false);
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, true);
		netService =
				new MRsLattice2DNetworkBuilder<MTestNode, MRepastEdge<MTestNode>>(
						new MRsEdgeFactory<MTestNode, MRepastEdge<MTestNode>>(), "TestNetwork");
		netService.setContext(new DefaultContext<MTestNode>());
		net = netService.buildNetwork(agents);
		
		assertEquals(NUM_AGENTS, net.numNodes());
		assertEquals((NUM_AGENTS - (int)Math.sqrt(NUM_AGENTS)) * 4, net.numEdges());

		edgeDistribution = new int[agents.size() + 1];
		for (MTestNode node : agents) {
			edgeDistribution[net.getDegree(node)]++;
		}
		assertEquals(4, edgeDistribution[4]);
		assertEquals(((int)Math.sqrt(NUM_AGENTS)-2)*4, edgeDistribution[6]);
		assertEquals(NUM_AGENTS - ((int)Math.sqrt(NUM_AGENTS)-2)*4 - 4, edgeDistribution[8]);
	}
}
