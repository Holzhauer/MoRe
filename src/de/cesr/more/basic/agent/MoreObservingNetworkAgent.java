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
package de.cesr.more.basic.agent;

/**
 * MORe
 * 
 * Enables MoRe to pass notifications about changes in a network that regard a specific node to that node.
 *
 * @author Sascha Holzhauer
 * @date 22 Jul 2014 
 *
 */
public interface MoreObservingNetworkAgent<AgentType> {

	/**
	 * MORe
	 * 
	 * Marker interface for network observations
	 * 
	 * @author Sascha Holzhauer
	 * @date 23 Jul 2014 
	 *
	 */
	public interface NetworkObservation {
	}

	/**
	 * MORe
	 *
	 * @author Sascha Holzhauer
	 * @date 23 Jul 2014 
	 *
	 */
	public enum NetworkObservations implements NetworkObservation {
		INCOMING_NEIGHBOUR_REMOVED,

		OUTGOING_NEIGHBOUR_REMOVED,

		INCOMING_NEIGHBOUR_ADDED,

		OUTGOING_NEIGHBOUR_ADDED;
	}

	/**
	 * MoRe calls this method in case of changes in the network regarding the node.
	 * @param observation
	 * @param object
	 */
	public void receiveNotification(NetworkObservation observation, AgentType object);
}
