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
 * Created by holzhauer on 24.06.2011
 */
package de.cesr.more.rs.building;

import java.util.Collection;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.building.MRsEdgeFactory;
import de.cesr.more.building.MoreEdgeFactory;
import de.cesr.more.networks.MoreRsNetwork;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.adapter.MRepastEdge;
import de.cesr.more.rs.adapter.MoreRsContextJungNetwork;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author holzhauer
 * @date 24.06.2011 
 *
 */
public class MRsWattsBetaSwBuilder<AgentType, EdgeType extends MRepastEdge<AgentType>> implements
		MoreRsNetworkBuilder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MRsWattsBetaSwBuilder.class);
	
	private Context<AgentType>	context;
	private MoreEdgeFactory<AgentType, EdgeType>	eFac;

	/**
	 * 
	 */
	public MRsWattsBetaSwBuilder() {
		this(new MRsEdgeFactory<AgentType, EdgeType>());
	}

	
	/**
	 * @param eFac
	 */
	public MRsWattsBetaSwBuilder(MRsEdgeFactory<AgentType, EdgeType> eFac) {
		this.eFac = eFac;
	}
	
	
	@Override
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection)
	 * Parameters are assigned through the parameter framework to allow network builders to be 
	 * initialises automatically.
	 */
	@SuppressWarnings("unchecked") // WattsBetaSmallWorldGenerator#createNetwork returns the network it receives
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {
		if (context == null) {
			logger.error("Context not set!");
			throw new IllegalStateException("Context not set!");
		}
		MoreRsNetwork<AgentType, EdgeType> network = new MoreRsContextJungNetwork<AgentType, EdgeType>(
				((Boolean)PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
						new DirectedJungNetwork<AgentType>("Network") :
				new UndirectedJungNetwork<AgentType>("Network")
				, context);
		network.setEdgeFactory(eFac);
		
		for (AgentType agent : agents) {
			network.addNode(agent);
		}
		
		network =  (MoreRsNetwork<AgentType, EdgeType>) new WattsBetaSmallWorldGenerator<AgentType>(
				((Double)PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_BETA)).doubleValue(),
				((Integer)PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_DEGREE)).intValue(),
				((Boolean)PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)))
				.createNetwork(network);
		return network;
	}
}

