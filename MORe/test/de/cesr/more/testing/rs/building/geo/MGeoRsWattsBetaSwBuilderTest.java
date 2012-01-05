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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;

import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MGeoRsWattsBetaSwBuilder;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MoreRsNetwork;
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
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		agents = new ArrayList<MoreMilieuAgent>(NUM_AGENTS);
		for (int i=0; i < NUM_AGENTS; i++) {
			agents.add(new MoreMilieuAgent(){
				@Override
				public int getMilieuGroup() {
					return 0;
				}

				@Override
				public String getAgentId() {
					return "id";
				}

				@Override
				public Context<?> getParentContext() {
					return null;
				}
			});
		}
	}

	/**
	 * Test method for {@link de.cesr.more.rs.building.MGeoRsWattsBetaSwBuilder#buildNetwork(java.util.Collection)}.
	 */
	@Test
	public void testBuildNetwork() {
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, new Boolean(false));
		MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> networkBuilder = new MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>>();
		Context<MoreMilieuAgent> context = new DefaultContext<MoreMilieuAgent>();
		for (MoreMilieuAgent o : agents) {
			context.add(o);
		}
		networkBuilder.setContext(context);
		MoreRsNetwork<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> network = networkBuilder.buildNetwork(agents);
		assertEquals(NUM_AGENTS, network.numNodes());
		assertEquals(NUM_AGENTS * ((Integer)PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG)).intValue() / 2, network.numEdges());

		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, new Boolean(true));
		networkBuilder = new MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>>();
		context = new DefaultContext<MoreMilieuAgent>();
		for (MoreMilieuAgent o : agents) {
			context.add(o);
		}
		networkBuilder.setContext(context);
		network = networkBuilder.buildNetwork(agents);
		assertEquals(NUM_AGENTS, network.numNodes());
		assertEquals(NUM_AGENTS * ((Integer)PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG)).intValue(), network.numEdges());
	}
	
	@Test
	public void testKProvider() {
		MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> networkBuilder = new MGeoRsWattsBetaSwBuilder<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>>();
		Context<MoreMilieuAgent> context = new DefaultContext<MoreMilieuAgent>();
		for (MoreMilieuAgent o : agents) {
			context.add(o);
		}
		networkBuilder.setContext(context);
		MoreRsNetwork<MoreMilieuAgent, MRepastEdge<MoreMilieuAgent>> network = networkBuilder.buildNetwork(agents);
	}
}
