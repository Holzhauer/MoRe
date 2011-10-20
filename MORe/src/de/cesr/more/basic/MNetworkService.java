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
 */
package de.cesr.more.basic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.IteratorUtils;
import org.apache.log4j.Logger;

import de.cesr.more.edges.MoreEdge;
import de.cesr.more.networks.MoreNetwork;

/**
 * MORe
 *
 * The MNetworkService provides some general static methods that are applied to networks.
 * 
 * @author holzhauer
 * @date 11.10.2011 
 *
 */
public class MNetworkService {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MNetworkService.class);

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
	
	/**
	 * Requires the same set of nodes to be equal (if B is representative of A, A.equals(B) must be true) 
	 * @param networkOne
	 * @param networkTwo
	 * @return true if the given networks are structurally equal.
	 */
	public static <AgentType> boolean isStructurallyEqual(MoreNetwork<AgentType, ?> networkOne, MoreNetwork<AgentType, ?> networkTwo) {
		Set<AgentType> twos = new HashSet<AgentType>();
		for (AgentType two : networkTwo.getNodes()) {
			twos.add(two);
		}
		for (AgentType one : networkOne.getNodes()) {
			if (!twos.contains(one)) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Network " + networkTwo + " does not contain " + one);
				}
				// LOGGING ->

				return false;
			}
			twos.remove(one);
			
			// check neighbours
			List<AgentType> oneSuccessors = IteratorUtils.toList(networkOne.getSuccessors(one).iterator());
			List<AgentType> twoSuccessors = IteratorUtils.toList(networkTwo.getSuccessors(one).iterator());
			if (!oneSuccessors.containsAll(twoSuccessors) || !twoSuccessors.containsAll(oneSuccessors)) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Successors of " + one + " not equal");
				}
				// LOGGING ->

				return false;
			}
			
			oneSuccessors = IteratorUtils.toList(networkOne.getPredecessors(one).iterator());
			twoSuccessors = IteratorUtils.toList(networkTwo.getPredecessors(one).iterator());
			if (!oneSuccessors.containsAll(twoSuccessors) || !twoSuccessors.containsAll(oneSuccessors)) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Predecessors of " + one + " not equal");
				}
				// LOGGING ->
				return false;
			}
		}
		if (twos.size() > 0) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("More nodes in " + networkTwo + "(" + twos + ")");
			}
			// LOGGING ->

			return false;
		}
		return true;
	}
}
