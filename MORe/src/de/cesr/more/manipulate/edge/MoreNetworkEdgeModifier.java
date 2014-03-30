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
 * Created by Sascha Holzhauer on 03.05.2011
 */
package de.cesr.more.manipulate.edge;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.rs.building.edge.MGeoRsNetworkEdgeModifier;

/**
 * MORe
 * 
 * The {@link MoreNetworkEdgeModifier} provides features to consistently add and remove
 * edges to or from a network. This is particularly important when using networks within
 * a geography (see {@link MGeoNetworkEdgeModifier}). It thus goes beyond a 
 * {@link MoreEdgeFactory} and rather uses {@link MoreEdgeFactory}s to create edges.
 *
 * @author Sascha Holzhauer
 * @date 03.05.2011 
 *
 */
public interface MoreNetworkEdgeModifier<AgentType, EdgeType extends MoreEdge<? super AgentType>> {
	
	/**
	 * Creates a new edge from source node to target node within the given
	 * network and takes care for additional work in the particular context, e.g.
	 * adding links in a geography.
	 * 
	 * @param network
	 * @param source
	 * @param target
	 * @return the (new) edge
	 */
	public EdgeType createEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source, AgentType target);
	
	/**
	 * Removes an edge from source node to target node within the given
	 * network and takes care for additional work in the particular context, e.g.
	 * removing links in a geography.
	 * @param network
	 * @param source
	 * @param target
	 * @return true if the deletion process was successful.
	 */
	public boolean removeEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source, AgentType target);
	
	/**
	 * @return the underlying edge factory
	 */
	public MoreEdgeFactory<AgentType, EdgeType> getEdgeFactory();
}
