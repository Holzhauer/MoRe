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
 * Created by Sascha Holzhauer on 23.05.2011
 */
package de.cesr.more.rs.building;



import java.util.Collection;

import repast.simphony.context.Context;

import de.cesr.more.building.MoreNetworkBuilder;
import de.cesr.more.networks.MoreRsNetwork;
import de.cesr.more.rs.adapter.MRepastEdge;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @param <AgentType>
 * @param <EdgeType>
 * @date 23.05.2011
 * 
 */
public interface MoreRsNetworkBuilder<AgentType, EdgeType extends MRepastEdge<AgentType>> extends
		MoreNetworkBuilder<AgentType, EdgeType> {

	/**
	 * @param agents
	 * @return Created by Sascha Holzhauer on 22.07.2010
	 */
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents);
	
	/**
	 * Set the context the resulting network is embedded within.
	 * @param context
	 * Created by Sascha Holzhauer on 23.05.2011
	 */
	public void setContext(Context<AgentType> context);

}
