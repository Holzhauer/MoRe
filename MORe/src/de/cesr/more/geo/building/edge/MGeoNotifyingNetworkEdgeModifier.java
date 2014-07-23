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
 * Created by Sascha Holzhauer on 22 Jul 2014
 */
package de.cesr.more.geo.building.edge;


import de.cesr.more.basic.agent.MoreObservingNetworkAgent;
import de.cesr.more.basic.agent.MoreObservingNetworkAgent.NetworkObservations;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.geo.MoreGeoEdge;
import de.cesr.more.manipulate.edge.MoreNotifyingNetworkEdgeModifier;


/**
 * TODO test
 * 
 * 
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 22 Jul 2014
 * 
 */
public class MGeoNotifyingNetworkEdgeModifier<AgentType extends MoreObservingNetworkAgent<AgentType>, EdgeType extends MoreGeoEdge<? super AgentType>>
		extends MGeoNetworkEdgeModifier<AgentType, EdgeType> implements
		MoreNotifyingNetworkEdgeModifier<AgentType, EdgeType> {

	public EdgeType createEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source, AgentType target) {
		target.receiveNotification(NetworkObservations.INCOMING_NEIGHBOUR_ADDED, source);
		source.receiveNotification(NetworkObservations.OUTGOING_NEIGHBOUR_ADDED, target);
		return super.createEdge(network, source, target);
	}

	public boolean removeEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source, AgentType target) {
		target.receiveNotification(NetworkObservations.INCOMING_NEIGHBOUR_REMOVED, source);
		source.receiveNotification(NetworkObservations.OUTGOING_NEIGHBOUR_REMOVED, target);
		return super.removeEdge(network, source, target);
	}
}
