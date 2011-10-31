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
 * Created by holzhauer on 31.10.2011
 */
package de.cesr.more.manipulate.network;

import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;

/**
 * Aggregates several nodes to a single one in combining links
 *
 * @author holzhauer
 * @date 31.10.2011 
 *
 */
public class MAggregator {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MAggregator.class);

	/**
	 * Aggregates two node to a single one (the first node given). It aggregates the links of both nodes
	 * at the surviving node and deletes the other node afterwards.
	 * 
	 * @param network
	 * @param survivingAgent
	 * @param otherAgent
	 * @return
	 */
	public static <AgentType, EdgeType extends MoreEdge<AgentType>> boolean
		aggregateNodes(MoreNetwork<AgentType, EdgeType> network, AgentType survivingAgent, AgentType otherAgent) {
		
		if (!network.containsNode(otherAgent)) {
			// <- LOGGING
			logger.error("Network " + network + " does not contain node to aggregate: " + otherAgent);
			// LOGGING ->
			throw new IllegalStateException("Network " + network + " does not contain node to aggregate: " + otherAgent);
		}
		if (!network.containsNode(otherAgent)) {
			// <- LOGGING
			logger.error("Network " + network + " does not contain node to aggregate: " + survivingAgent);
			// LOGGING ->
			throw new IllegalStateException("Network " + network + " does not contain node to aggregate: " + survivingAgent);
		}
		
		if (network.getPredecessors(otherAgent) != null) {
			for (AgentType partner : network.getPredecessors(otherAgent)) {
				if (!partner.equals(survivingAgent) && !network.isSuccessor(partner, survivingAgent)) {
					network.connect(partner, survivingAgent);
				}
			}
		}
		if (network.getSuccessors(otherAgent) != null) {
			for (AgentType partner : network.getSuccessors(otherAgent)) {
				if (!partner.equals(survivingAgent) && !network.isSuccessor(survivingAgent, partner)) {
					network.connect(survivingAgent, partner);
				}
			}
		}
		network.removeNode(otherAgent);
		return true;
	}
}
