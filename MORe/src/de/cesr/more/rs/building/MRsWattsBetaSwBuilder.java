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

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.building.MRsEdgeFactory;
import de.cesr.more.building.MoreEdgeFactory;
import de.cesr.more.networks.MoreRsNetwork;
import de.cesr.more.rs.adapter.MRepastEdge;

/**
 * MORe
 *
 * @author holzhauer
 * @date 24.06.2011 
 *
 */
public class MRsWattsBetaSwBuilder<AgentType, EdgeType extends MRepastEdge<AgentType>> implements
		MoreRsNetworkBuilder<AgentType, EdgeType> {

	private Context	context;
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
	public MRsWattsBetaSwBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this.eFac = eFac;
	}
	
	
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(Collection agents) {
		ContextJungNetwork<AgentType> network = new ContextJungNetwork<AgentType>(new UndirectedJungNetwork<AgentType>(
				"Network"), context);
		network = (ContextJungNetwork<AgentType>) new WattsBetaSmallWorldGenerator<AgentType>(0.1, 5, false)
				.createNetwork(network);
		return null;
	}

}

