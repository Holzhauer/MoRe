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

import cern.jet.random.Uniform;

import repast.simphony.context.Context;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MSmallWorldBetaModelNetworkGenerator;
import de.cesr.more.building.network.MSmallWorldBetaModelNetworkGenerator.MSmallWorldBetaModelNetworkGeneratorParams;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.edge.MRsEdgeFactory;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.uranus.core.URandomService;

/**
 * MORe
 * 
 * @author holzhauer
 * @author Jung Project
 * @author Nick Collier (Repast Simphony)
 * 
 * @date 24.06.2011
 * 
 */
public class MRsWattsBetaSwBuilder<AgentType, EdgeType extends MRepastEdge<AgentType>>
		implements MoreRsNetworkBuilder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MRsWattsBetaSwBuilder.class);

	private Context<AgentType> context;
	private MoreEdgeFactory<AgentType, EdgeType> eFac;

	
	protected Uniform randomDist;
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
		this.randomDist = URandomService.getURandomService().getUniform();
	}

	@Override
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection)
	 *      Parameters are assigned through the parameter framework to allow
	 *      network builders to be initialises automatically.
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {
		
		if (context == null) {
			logger.error("Context not set!");
			throw new IllegalStateException("Context not set!");
		}
		
		MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ? new DirectedJungNetwork<AgentType>(
						"Network") : new UndirectedJungNetwork<AgentType>(
						"Network"), context);
		
		MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType> params = 
			new MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType>();
		
		params.setNetwork(network);
		params.setEdgeFactory(eFac);
		params.setRandomDist(randomDist);
		
		MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType> gen = new MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType>(params);
		
		return (MoreRsNetwork<AgentType, EdgeType>) gen.buildNetwork(agents);



	}
}