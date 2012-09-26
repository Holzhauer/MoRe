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
 * Created by holzhauer on 27.09.2011
 */
package de.cesr.more.testing.rs.building.geo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import de.cesr.more.basic.MManager;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MGeoRsWattsBetaSwBuilder;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.more.testing.testutils.MTestGraphs;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author holzhauer
 * @date 27.09.2011 
 *
 */
public class MGeoRsWattsBetaSwBuilderTest {

	static final int NUM_AGENTS = 20;
	protected Collection<MoreMilieuAgent> agents;
	protected Context<MoreMilieuAgent>		context;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MManager.init();
		agents = new ArrayList<MoreMilieuAgent>(NUM_AGENTS);

		context = new DefaultContext<MoreMilieuAgent>();

		for (int i=0; i < NUM_AGENTS; i++) {
			agents.add(new MTestGraphs.MTestNode(1));
		}

		for (MoreMilieuAgent o : agents) {
			context.add(o);
		}
	}

	/**
	 * Test method for {@link de.cesr.more.rs.building.MGeoRsWattsBetaSwBuilder#buildNetwork(java.util.Collection)}.
	 */
	@Test
	public void testBuildNetwork() {
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, new Boolean(false));
		PmParameterManager.setParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS, null);

		MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> networkBuilder = new MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>>();

		networkBuilder.setContext(context);
		MoreRsNetwork<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> network = networkBuilder.buildNetwork(agents);

		assertEquals(NUM_AGENTS, network.numNodes());
		assertEquals(NUM_AGENTS * ((Integer)PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG)).intValue() / 2, network.numEdges());

		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, new Boolean(true));
		networkBuilder = new MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>>();
		networkBuilder.setContext(context);
		network = networkBuilder.buildNetwork(agents);
		assertEquals(NUM_AGENTS, network.numNodes());
		assertEquals(NUM_AGENTS * ((Integer)PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG)).intValue(), network.numEdges());
	}
	
	@Test
	public void testAddAndLinkNode() {
		// build undirected network
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, new Boolean(false));
		PmParameterManager.setParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS, null);

		MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> networkBuilder = new MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>>();
		networkBuilder.setContext(context);

		MoreRsNetwork<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> network = networkBuilder.buildNetwork(agents);

		// add node
		MTestGraphs.MTestNode newAgent = new MTestGraphs.MTestNode(1);
		context.add(newAgent);
		networkBuilder.addAndLinkNode(network, newAgent);

		// check in-degree
		assertEquals(
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG)).intValue(),
				network.getInDegree(newAgent));

		// remove node
		Map<MoreMilieuAgent, Integer> exPartners = new HashMap<MoreMilieuAgent, Integer>();
		for (MoreMilieuAgent partner : network.getPredecessors(newAgent)) {
			exPartners.put(partner, new Integer(network.getOutDegree(partner)));
		}
		network.removeNode(newAgent);

		// check out-degree of ex-partners
		for (MoreMilieuAgent partner : exPartners.keySet()) {
			assertEquals((exPartners.get(partner) - 1), network.getOutDegree(partner));
		}

		// build directed network
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, new Boolean(true));
		networkBuilder = new MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>>();
		networkBuilder.setContext(context);
		network = networkBuilder.buildNetwork(agents);

		// add node
		newAgent = new MTestGraphs.MTestNode(1);
		context.add(newAgent);
		networkBuilder.addAndLinkNode(network, newAgent);

		// check in-degree
		assertEquals(
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG)).intValue(),
				network.getInDegree(newAgent));

		// remove node
		exPartners = new HashMap<MoreMilieuAgent, Integer>();
		for (MoreMilieuAgent partner : network.getPredecessors(newAgent)) {
			exPartners.put(partner, new Integer(network.getOutDegree(partner)));
		}
		network.removeNode(newAgent);

		// check out-degree of ex-partners
		for (MoreMilieuAgent partner : exPartners.keySet()) {
			assertEquals((exPartners.get(partner) - 1), network.getOutDegree(partner));
		}
	}

	@Test
	public void testKProvider() {
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, new Boolean(true));
		MMilieuNetworkParameterMap netParamMap = new MMilieuNetworkParameterMap();
		netParamMap.setK(1, 10);
		PmParameterManager.setParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS, netParamMap);

		MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> networkBuilder = new MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>>();
		networkBuilder.setContext(context);
		MoreRsNetwork<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> network = networkBuilder.buildNetwork(agents);
		assertEquals(NUM_AGENTS, network.numNodes());
		assertEquals(NUM_AGENTS * 10, network.numEdges());
	}

	@Test
	public void testConsiderSources() {
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, new Boolean(true));
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_WSSM_CONSIDER_SOURCES, new Boolean(true));
		PmParameterManager.setParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS, null);

		MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> networkBuilder = new MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>>();
		networkBuilder.setContext(context);
		networkBuilder.setContext(context);
		MoreRsNetwork<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> network = networkBuilder.buildNetwork(agents);
		assertEquals(NUM_AGENTS, network.numNodes());
		assertEquals(
				NUM_AGENTS
						* ((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG)).intValue(),
				network.numEdges());
	}
}
