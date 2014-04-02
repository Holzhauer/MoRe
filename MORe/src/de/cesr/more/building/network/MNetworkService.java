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
 * Created by holzhauer on 21.11.2011
 */
package de.cesr.more.building.network;


import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.manipulate.edge.MDefaultNetworkEdgeModifier;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.util.Log4jLogger;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * Provides basic functionality for network generators
 * 
 * @author holzhauer
 * @date 21.11.2011
 * 
 */
public abstract class MNetworkService<AgentType, EdgeType extends MoreEdge<? super AgentType>> implements
		MoreNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger									logger	= Log4jLogger.getLogger(MNetworkService.class);

	protected MoreEdgeFactory<AgentType, EdgeType>			edgeFac	= null;

	protected String										name;

	protected PmParameterManager							pm;

	/**
	 * should be accessed via getEdgeModifer...
	 */
	protected MoreNetworkEdgeModifier<AgentType, EdgeType>	edgeModifier;

	/**
	 * @param areasGeography
	 */
	public MNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, PmParameterManager pm) {
		this.edgeFac = edgeFac;
		this.edgeModifier = new MDefaultNetworkEdgeModifier<AgentType, EdgeType>(edgeFac);
		this.pm = pm;
	}

	/**
	 * @param areasGeography
	 */
	public MNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(edgeFac, PmParameterManager.getInstance(null));
	}

	/**
	 * @param areasGeography
	 */
	@SuppressWarnings("unchecked")
	// risky but not avoidable
	public MNetworkService() {
		this((MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>());
	}

	/**
	 * Adds agents as nodes to the given network without linking them.
	 * 
	 * @param network
	 * @param agents
	 */
	protected void addAgents(MoreNetwork<AgentType, EdgeType> network, Collection<AgentType> agents) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Adding agents to network: " + agents);
		}
		// LOGGING ->

		for (AgentType o : agents) {
			network.addNode(o);
		}
	}

	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#removeNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	@Override
	public boolean removeNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		Collection<AgentType> partners = new HashSet<AgentType>();
		for (AgentType partner : network.getSuccessors(node)) {
			partners.add(partner);
		}
		for (AgentType partner : partners) {
			edgeModifier.removeEdge(network, node, partner);
		}

		partners.clear();

		for (AgentType partner : network.getPredecessors(node)) {
			partners.add(partner);
		}
		for (AgentType partner : partners) {
			edgeModifier.removeEdge(network, partner, node);
		}
		network.removeNode(node);
		return false;
	}

	/**
	 * @see de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier#removeEdge(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean removeEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source, AgentType target) {
		return getEdgeModifier().removeEdge(network, source, target);
	}

	/**
	 * Created an edge in the direction from potInfluencer to influencedHh
	 * 
	 * @param network
	 * @param influencedHh
	 * @param potInfluencer
	 */
	@Override
	public EdgeType createEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source,
				AgentType target) {
		return getEdgeModifier().createEdge(network, source, target);
	}

	/**
	 * @param network
	 */
	protected void logEdges(Logger logger, MoreNetwork<AgentType, EdgeType> network, String prestring) {
		if (logger.isDebugEnabled()) {
			Set<MoreEdge<? super AgentType>> edges = new TreeSet<MoreEdge<? super AgentType>>(
						new Comparator<MoreEdge<? super AgentType>>() {

							@Override
							public int compare(MoreEdge<? super AgentType> o1,
									MoreEdge<? super AgentType> o2) {
								if (!o1.getEnd().toString()
										.equals(o2.getEnd().toString())) {
									return o1.getEnd().toString()
											.compareTo(o2.getEnd().toString());
								} else {
									return o1.getStart().toString()
											.compareTo(o2.getStart().toString());
								}
							}
						});
			edges.addAll(network.getEdgesCollection());
			for (MoreEdge<? super AgentType> edge : edges) {
				logger.debug(prestring + edge);
			}
		}
	}

	protected void checkAgentCollection(Collection<AgentType> agents) {
		// check agent collection:
		if (!(agents instanceof Set)) {
			Set<AgentType> set = new HashSet<AgentType>();
			set.addAll(agents);
			if (set.size() != agents.size()) {
				logger.error("Agent collection contains duplicate entries of at least one agent " +
							"(Set site: " + set.size() + "; collection size: " + agents.size());
				throw new IllegalStateException("Agent collection contains duplicate entries of at least one agent "
						+ "(Set site: " + set.size() + "; collection size: " + agents.size());
			}
		}
	}

	/*************************************
	 * GETTER & SETTER
	 *************************************/

	/**
	 * @return the edgeModifier
	 */
	public MoreNetworkEdgeModifier<AgentType, EdgeType> getEdgeModifier() {
		if (edgeModifier == null) {
			logger.error("Edge Modifier has not been set or could not be instantiated since geography was not set!");
			throw new IllegalArgumentException(
					"Edge Modifier has not been set or could not be instantiated since geography was not set!");
		}
		return edgeModifier;
	}

	/**
	 * @param edgeModifier
	 *        the edgeModifier to set
	 */
	@Override
	public void setEdgeModifier(
				MoreNetworkEdgeModifier<AgentType, EdgeType> edgeModifier) {
		this.edgeModifier = edgeModifier;
	}

	/**
	 * To set a new {@link MoreEdgeFactory}, assign a new {@link MoreNetworkEdgeModifier}!
	 * 
	 * @see de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier#getEdgeFactory()
	 */
	@Override
	public MoreEdgeFactory<AgentType, EdgeType> getEdgeFactory() {
		return edgeFac;
	}
}