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
 * Created by Sascha Holzhauer on 18 Aug 2014
 */
package de.cesr.more.building.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.util.Log4jLogger;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 18 Aug 2014 
 *
 */
public class MNetworkBuilder<AgentType, EdgeType extends MoreEdge<AgentType>> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MNetworkBuilder.class);
	
	protected MoreNetworkEdgeModifier<AgentType, EdgeType> edgeModifier;
	
	protected PmParameterManager pm;
	
	protected String name;
	
	
	/**
	 * @param edgeModifier
	 * @param name
	 */
	public MNetworkBuilder(MoreNetworkEdgeModifier<AgentType, EdgeType> edgeModifier, String name, 
			PmParameterManager pm) {
		this.edgeModifier = edgeModifier;
		this.name = name;
		this.pm = pm;
	}
	
	
	/**
	 * @param agents
	 */
	protected void checkAgentCollection(Collection<AgentType> agents) {
		// check agent collection:
		if (!(agents instanceof Set)) {
			Set<AgentType> set = new HashSet<AgentType>();
			set.addAll(agents);
			if (set.size() != agents.size()) {
				logger.error("Agent collection contains duplicate entries of at least one agent " +
							"(Set site: " + set.size() + "; collection size: " + agents.size());
				throw new IllegalStateException("Agent collection contains duplicate entries of at least one agent " +
							"(Set site: " + set.size() + "; collection size: " + agents.size());
			}
		}
	}

}
