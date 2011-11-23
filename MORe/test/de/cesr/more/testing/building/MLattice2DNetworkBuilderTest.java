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
 * Created by holzhauer on 22.11.2011
 */
package de.cesr.more.testing.building;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.network.MLattice2DNetworkBuilder;
import de.cesr.more.building.network.MoreNetworkBuilder;
import de.cesr.more.param.MNetBuildLattice2DPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.parma.core.PmParameterManager;
import static org.junit.Assert.assertEquals;

/**
 * MORe
 *
 * @author holzhauer
 * @date 22.11.2011 
 *
 */
public class MLattice2DNetworkBuilderTest {
	
	MoreNetwork<Object, MoreEdge<Object>> network;

	Collection<Object> agents = new ArrayList<Object>(9);
	MoreNetworkBuilder<Object, MoreEdge<Object>> networkBuilder;
	
	@Before
	public void setup(){
		for (int i = 0; i < 9; i++) {
			agents.add(new Object());
		}
	}
	
	@Test
	public void testBuildingDirected() {
		
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, Boolean.TRUE);
		PmParameterManager.setParameter(MNetBuildLattice2DPa.TOROIDAL, Boolean.TRUE);
		
		MoreNetworkBuilder<Object, MoreEdge<Object>> networkBuilder = new MLattice2DNetworkBuilder<Object, MoreEdge<Object>>(
				new MDefaultEdgeFactory<Object>(), "Network");
		

		network = networkBuilder.buildNetwork(agents);
		
		assertEquals(9, network.numNodes());
		assertEquals(9*4, network.numEdges());
	}
		
	@Test
	public void testBuildingUndirected() {
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, Boolean.FALSE);
		PmParameterManager.setParameter(MNetBuildLattice2DPa.TOROIDAL, Boolean.TRUE);
		
		networkBuilder = new MLattice2DNetworkBuilder<Object, MoreEdge<Object>>(
				new MDefaultEdgeFactory<Object>(), "Network");
		

		network = networkBuilder.buildNetwork(agents);
		
		assertEquals(9, network.numNodes());
		assertEquals(9*2, network.numEdges());
	}
		
		
	@Test
	public void testBuildingNoneToroidal() {
		PmParameterManager.setParameter(MNetworkBuildingPa.BUILD_DIRECTED, Boolean.FALSE);
		PmParameterManager.setParameter(MNetBuildLattice2DPa.TOROIDAL, Boolean.FALSE);
		
		networkBuilder = new MLattice2DNetworkBuilder<Object, MoreEdge<Object>>(
				new MDefaultEdgeFactory<Object>(), "Network");
		

		network = networkBuilder.buildNetwork(agents);
		
		assertEquals(9, network.numNodes());
		assertEquals(12, network.numEdges());
	}
}
