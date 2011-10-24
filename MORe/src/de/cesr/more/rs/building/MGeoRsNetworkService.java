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
 * Created by holzhauer on 23.09.2011
 */
package de.cesr.more.rs.building;


import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.RepastEdge;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.cesr.more.util.Log4jLogger;
import de.cesr.more.util.io.MoreIoUtilities;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.geo.MoreGeoEdge;
import de.cesr.more.geo.building.MoreGeoNetworkBuilder;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author holzhauer
 * @date 23.09.2011 
 *
 */
public abstract class MGeoRsNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends RepastEdge<AgentType> & MoreEdge<AgentType>> implements
		MoreRsNetworkService<AgentType, EdgeType>,
		MoreGeoNetworkBuilder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger			logger			= Log4jLogger.getLogger(MGeoRsNetworkService.class);

	
	/**
	 * Need to be of type {@link Object} since network objects and agents should be insertable
	 */
	protected Geography<Object>		geography;
	
	protected GeometryFactory		geoFactory		= null;
	
	protected MoreEdgeFactory<AgentType, EdgeType> edgeFac = null;

	/**
	 * The context the network belongs to.
	 */
	protected Context<AgentType>						   context;
	
	/**
	 * should be accessed via getEdgeModifer...
	 */
	private MGeoRsNetworkEdgeModifier<AgentType, EdgeType> edgeModifier;
	

	/**
	 * @param areasGeography
	 */
	public MGeoRsNetworkService(Geography<Object> areasGeography, MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this.geography = areasGeography;
		this.edgeFac = edgeFac;
		this.geoFactory = new GeometryFactory(new PrecisionModel(),
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.SPATIAL_REFERENCE_ID)).intValue());
	}

	/**
	 * @param areasGeography
	 */
	public MGeoRsNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(null, edgeFac);
	}
	
	/**
	 * @param areasGeography
	 */
	public MGeoRsNetworkService() {
		this(null, (MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>());
	}
	
	/**
	 * Adds agents as nodes to the given network without linking them.
	 * @param network
	 * @param agents
	 */
	protected void addAgents(MoreRsNetwork<AgentType, EdgeType> network, Collection<AgentType> agents) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Adding agents to collection.");
		}
		// LOGGING ->

		for (AgentType o : agents) {
			network.addNode(o);
		}
	}
	
	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#removeNode(de.cesr.more.basic.network.MoreNetwork, java.lang.Object)
	 */
	@Override
	public boolean removeNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		for (AgentType partner : network.getSuccessors(node)) {
			edgeModifier.removeEdge(network, node, partner);
		}
		for (AgentType partner : network.getPredecessors(node)) {
			edgeModifier.removeEdge(network, partner, node);
		}
		network.removeNode(node);
		return false;
	}
	

	/**
	 * @param agents the agents that are to be connected by the network builder
	 * @param name the network's name
	 * 
	 * @return a network
	 */
	public abstract MoreRsNetwork<AgentType, EdgeType> buildRsNetwork(Collection<AgentType> agents, String name);

	/**
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {
		return buildRsNetwork(agents, "Network");
	}

	/**
	 * @see de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier#removeEdge(de.cesr.more.basic.network.MoreNetwork, java.lang.Object, java.lang.Object)
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
	protected void outputNetwork(MoreNetwork<AgentType, EdgeType> network) {
		File file1 = new File(((String) PmParameterManager.getParameter(MNetworkBuildingPa.NETWORK_TARGET_FILE)));
		MoreIoUtilities.<AgentType, EdgeType> outputGraph(network, file1);
	}

	/**
	 * @param network
	 */
	protected void logEdges(MoreRsNetwork<AgentType, EdgeType> network, String prestring) {
		if (logger.isDebugEnabled()) {
			Set<MoreEdge<AgentType>> edges = new TreeSet<MoreEdge<AgentType>>(
					new Comparator<MoreEdge<AgentType>>() {

						@Override
						public int compare(MoreEdge<AgentType> o1,
								MoreEdge<AgentType> o2) {
							// if
							// (!o1.getStart().getAgentID().equals(o2.getStart().getAgentID()))
							// {
							// return
							// o1.getStart().getAgentID().compareTo(o2.getStart().getAgentID());
							// } else {
							// return
							// o1.getEnd().getAgentID().compareTo(o2.getEnd().getAgentID());
							// }
							if (!o1.getEnd().getAgentId()
									.equals(o2.getEnd().getAgentId())) {
								return o1.getEnd().getAgentId()
										.compareTo(o2.getEnd().getAgentId());
							} else {
								return o1.getStart().getAgentId()
										.compareTo(o2.getStart().getAgentId());
							}
						}
					});
			edges.addAll(network.getEdgesCollection());
			for (MoreEdge<AgentType> edge : edges) {
				logger.debug(prestring + edge);
			}
		}
	}
	
	/*************************************
	 *   GETTER & SETTER
	 *************************************/
	
	/**
	 * @see de.cesr.more.geo.building.MoreGeoNetworkBuilder#setGeograpy(repast.simphony.space.gis.Geography)
	 */
	@Override
	public void setGeograpy(Geography<Object> geography) {
		this.geography = geography;
		this.edgeModifier = new MGeoRsNetworkEdgeModifier<AgentType, EdgeType>(this.edgeFac, geography, geoFactory);
	}
	
	/**
	 * Set the (root) context the network shall span
	 * 
	 * @param context
	 */
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}

	/**
	 * @return the edgeModifier
	 */
	public MGeoRsNetworkEdgeModifier<AgentType, EdgeType> getEdgeModifier() {
		if (edgeModifier == null) {
			logger.error("Edge Modifier has not been set or could not be instantiated since geography was not set!");
			throw new IllegalArgumentException("Edge Modifier has not been set or could not be instantiated since geography was not set!");
		}
		return edgeModifier;
	}

	/**
	 * @param edgeModifier the edgeModifier to set
	 */
	public void setEdgeModifier(
			MGeoRsNetworkEdgeModifier<AgentType, EdgeType> edgeModifier) {
		this.edgeModifier = edgeModifier;
	}
}