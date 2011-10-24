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
 * Created by Sascha Holzhauer on 14.06.2011
 */
package de.cesr.more.manipulate.network;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.network.MoreNetworkBuilder;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.rs.network.MoreRsNetwork;

/**
 * MORe
 *
 * The  MoreNetworkModeifier interface provides methods to add agents to and remove agents from
 * an existing network. It is especially useful in combination with {@link MoreNetworkBuilder}
 * (see) 
 * @author Sascha Holzhauer
 * @param <AgentType> 
 * @param <EdgeType> 
 * @date 14.06.2011 
 *
 */
public interface MoreNetworkModifier<AgentType, EdgeType extends MoreEdge<AgentType>> 
	extends MoreNetworkEdgeModifier<AgentType, EdgeType>{

	/**
	 * Removes an agent from the given network and deletes all its links. Basically,
	 * this method reverses all the action that {@link #addAgent(Object, MoreRsNetwork)}
	 * performed.
	 * @param agent the agent to remove
	 * @param network the network to remove the given agent from
	 * @return true if the agent could be completely removed
	 */
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network, AgentType node);
	
	/**
	 * Adds an agent to the given network. In combination with {@link MoreNetworkBuilder}
	 * this method should connect the agent in a way the 
	 * @param agent the agent to add
	 * @param network the network the given agent is added to
	 * @return true if the agent could be added an all additional steps could be performed
	 */
	public boolean removeNode(MoreNetwork<AgentType, EdgeType> network, AgentType node);
}
