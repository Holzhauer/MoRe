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
package de.cesr.more.rs.building;

import java.util.Collection;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.geo.manipulate.MoreGeoNetworkEdgeModifier;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MLattice2DNetworkBuilder;
import de.cesr.more.building.util.MLattice2DGenerator;
import de.cesr.more.param.MNetBuildLattice2DPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.edge.MGeoRsNetworkEdgeModifier;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author holzhauer
 * @date 22.11.2011 
 *
 */
public class MGeoRsLattice2DNetworkBuilder<AgentType, EdgeType extends MRepastEdge<AgentType>> extends
MLattice2DNetworkBuilder<AgentType, EdgeType> implements MoreGeoRsNetworkBuilder<AgentType, EdgeType>{

	
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger( MGeoRsLattice2DNetworkBuilder.class);
	
	protected Context<AgentType>	context;
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked") // risky but unavoidable
	public MGeoRsLattice2DNetworkBuilder() {
		this((MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>(), "Network");
	}

	public MGeoRsLattice2DNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(eFac, name);
		this.edgeModifier = new MGeoRsNetworkEdgeModifier<AgentType, EdgeType>(eFac);
		this.latticeGenerator = new MLattice2DGenerator<AgentType, EdgeType>(
				(Boolean)PmParameterManager.getParameter(MNetBuildLattice2DPa.TOROIDAL));
	}
	
	/**
	 * The order of elements in agents influences the lattice's characteristic.
	 * So, make sure to shuffle the collection unless you require a special grid!
	 * 
	 * Furthermore in this regard, make sure the network underlying graph is an ordered one
	 * (however, one that does not use HashMaps to store vertices), like
	 * UndirectedOrderedSparseMultigraph (RS's UndirectedJungNetworks and DirectedJungNetwork
	 * use such ordered graphs)!
	 * 
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {
		
		if (context == null) {
			// <- LOGGING
			logger.error("The context has not been set!");
			// LOGGING ->
			throw new IllegalStateException("The context has not bee set!");
		}
		
		MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType >(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
						new DirectedJungNetwork<AgentType>(name) :
						new UndirectedJungNetwork<AgentType>(name), context, this.edgeModifier.getEdgeFactory());
		for (AgentType agent : agents) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Add agent " + agent + " to network.");
			}
			// LOGGING ->

			network.addNode(agent);
		}
		network = (MoreRsNetwork<AgentType, EdgeType>) latticeGenerator.createNetwork(network, edgeModifier);
		return  network;
	}
	
	@Override
	public void setGeography(Geography<Object> geography) {
		((MoreGeoNetworkEdgeModifier<AgentType, EdgeType>) this.edgeModifier).setGeography(geography);
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#setContext(repast.simphony.context.Context)
	 */
	@Override
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}
}
