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
 * Created by Sascha Holzhauer on 16.03.2012
 */
package de.cesr.more.rs.building;


import java.util.Collection;

import edu.uci.ics.jung.graph.Graph;


/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 16.03.2012
 * 
 */
public interface MorePartnerFinder<AgentType, EdgeType> {

	/**
	 * Finds a partner for the given focal agent in the given graph. Sets incoming to true.
	 * 
	 * @param graph
	 * @param focal
	 * @return
	 */
	public AgentType findPartner(Graph<AgentType, EdgeType> graph, AgentType focal);

	/**
	 * Finds a partner for the given focal agent in the given graph.
	 * 
	 * @param graph
	 * @param focal
	 * @param incoming
	 *        true if a predecessor is requested, false for successor
	 * @return
	 */
	public AgentType findPartner(Graph<AgentType, EdgeType> graph, AgentType focal, boolean incoming);

	/**
	 * Selects a partner for the given focal agent from the given list of potential partners. Checks whether the focal
	 * agent is already linked to the potential partner.
	 * 
	 * @param agents
	 *        should be a determined-randomisation-secure collection
	 * @param graph
	 * @param focal
	 * @return
	 */
	public AgentType findPartner(Collection<AgentType> agents, Graph<AgentType, EdgeType> graph, AgentType focal,
			boolean incoming);
}
