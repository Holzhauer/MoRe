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
 * Created by Sascha Holzhauer on 02.04.2012
 */
package de.cesr.more.rs.building;


import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.param.MRandomPa;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.graph.Graph;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 02.04.2012 
 *
 */
public class MDefaultPartnerFinder<AgentType, EdgeType> implements MorePartnerFinder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MDefaultPartnerFinder.class);

	protected Uniform		rand;

	/**
	 * @see de.cesr.more.rs.building.MorePartnerFinder#findPartner(edu.uci.ics.jung.graph.Graph, java.lang.Object)
	 */
	@Override
	public AgentType findPartner(Graph<AgentType, EdgeType> graph, AgentType focal) {
		return findPartner(graph.getVertices(), graph, focal, true);
	}

	/**
	 * @see de.cesr.more.rs.building.MorePartnerFinder#findPartner(edu.uci.ics.jung.graph.Graph, java.lang.Object,
	 *      boolean)
	 */
	@Override
	public AgentType findPartner(Graph<AgentType, EdgeType> graph, AgentType focal, boolean incoming) {
		return findPartner(graph.getVertices(), graph, focal, incoming);
	}

	/**
	 * @see de.cesr.more.rs.building.MorePartnerFinder#findPartner(java.util.Collection, edu.uci.ics.jung.graph.Graph,
	 *      java.lang.Object, boolean)
	 */
	@Override
	public AgentType findPartner(Collection<AgentType> agents, Graph<AgentType, EdgeType> graph, AgentType focal,
			boolean incoming) {
		ArrayList<AgentType> list = new ArrayList<AgentType>(agents);
		AgentType partner = null;
		do {
			partner = list.get(rand.nextIntFromTo(0, list.size() - 1));
		} while (partner == focal || incoming && graph.isPredecessor(partner, focal) || !incoming
				&& graph.isSuccessor(partner, partner));
		return partner;
	}

	protected Uniform getRandomDist() {
		if (this.rand == null) {
			AbstractDistribution abstractDis = MManager
					.getURandomService()
					.getDistribution(
							(String) PmParameterManager
									.getParameter(MRandomPa.RND_UNIFORM_DIST_NETWORK_BUILDING));

			if (abstractDis instanceof Uniform) {
				this.rand = (Uniform) abstractDis;
			} else {
				this.rand = MManager.getURandomService().getUniform();
				logger.warn("Use default uniform distribution");
			}
		}
		return this.rand;
	}
}
