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
package de.cesr.more.manipulate.edge;

import org.apache.log4j.Logger;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;

/**
 * MORe
 *
 * @author holzhauer
 * @date 22.11.2011 
 *
 */
public class MDefaultNetworkEdgeModifier<AgentType, EdgeType extends MoreEdge<? super AgentType>> implements
		MoreNetworkEdgeModifier<AgentType, EdgeType> {
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MDefaultNetworkEdgeModifier.class);
	
	protected MoreEdgeFactory<AgentType, EdgeType> edgeFac;
	
	public MDefaultNetworkEdgeModifier(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this.edgeFac = edgeFac;
	}

	/**
	 * @see de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier#createEdge(java.lang.Object, java.lang.Object, boolean)
	 */
	@Override
	public EdgeType createEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source, AgentType target) {
		EdgeType edge = edgeFac.createEdge(source, target, network.isDirected());
		network.connect(edge);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Edge added: " + edge);
		}
		// LOGGING ->
		return edge;
	}

	/**
	 * @see de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier#removeEdge(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean removeEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source, AgentType target) {
		EdgeType edge = network.disconnect(source, target);
		if (edge != null) {
			return true;
		}
		return false;
	}

	/**
	 * @see de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier#getEdgeFactory()
	 */
	@Override
	public MoreEdgeFactory<AgentType, EdgeType> getEdgeFactory() {
		return edgeFac;
	}
}
