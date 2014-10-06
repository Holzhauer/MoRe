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
 * Created by Sascha Holzhauer on 13.06.2012
 */
package de.cesr.more.rs.building;


import repast.simphony.context.Context;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MNetworkService;
import de.cesr.more.manipulate.edge.MDefaultNetworkEdgeModifier;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * Adds context facility to {@link MNetworkService} and requires {@link MRepastEdge}s.
 * 
 * @author Sascha Holzhauer
 * @date 13.06.2012
 * 
 */
public abstract class MRsNetworkService<AgentType, EdgeType extends MRepastEdge<AgentType>> extends
		MNetworkService<AgentType, EdgeType>
		implements MoreRsNetworkService<AgentType, EdgeType> {

	/**
	 * @param areasGeography
	 */
	public MRsNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(edgeFac, PmParameterManager.getInstance(null));
	}
	
	/**
	 * @param areasGeography
	 */
	public MRsNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, PmParameterManager pm) {
		super(edgeFac, pm);
		this.edgeModifier = new MDefaultNetworkEdgeModifier<AgentType, EdgeType>(edgeFac);
	}

	/**
	 * @param areasGeography
	 */
	@SuppressWarnings("unchecked")
	// risky but not avoidable
	public MRsNetworkService() {
		this((MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>());
	}

	/**
	 * The context the network belongs to.
	 */
	protected Context<AgentType>	context;

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#setContext(repast.simphony.context.Context)
	 */
	@Override
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}
}
