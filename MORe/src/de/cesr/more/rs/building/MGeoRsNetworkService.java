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


import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.RepastEdge;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.cesr.more.util.Log4jLogger;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MNetworkService;
import de.cesr.more.geo.building.MoreGeoNetworkBuilder;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.edge.MGeoRsNetworkEdgeModifier;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;

/**
 * Removal and Addition of nodes and agents to geo-referenced networks.
 *
 * @author holzhauer
 * @date 23.09.2011 
 *
 */
public abstract class MGeoRsNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends RepastEdge<AgentType> & MoreEdge<AgentType>>
	extends MNetworkService<AgentType, EdgeType> implements	MoreGeoNetworkBuilder<AgentType, EdgeType> {

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
	 * @param areasGeography
	 */
	public MGeoRsNetworkService(Geography<Object> areasGeography, MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		super(edgeFac);
		this.geography = areasGeography;
		this.geoFactory = new GeometryFactory(new PrecisionModel(),
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.SPATIAL_REFERENCE_ID)).intValue());
	}

	/**
	 * @param areasGeography
	 */
	public MGeoRsNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this((Geography)PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac);
	}
	
	/**
	 * @param areasGeography
	 */
	@SuppressWarnings("unchecked") // risky but not avoidable
	public MGeoRsNetworkService() {
		this(null, (MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>());
	}


	/**
	 * Specify super type method for MilieuAgents
	 * @param network
	 */
	protected void logEdges(MoreRsNetwork<AgentType, EdgeType> network, String prestring) {
		if (logger.isDebugEnabled()) {
			Set<MoreEdge<AgentType>> edges = new TreeSet<MoreEdge<AgentType>>(
					new Comparator<MoreEdge<AgentType>>() {

						@Override
						public int compare(MoreEdge<AgentType> o1,
								MoreEdge<AgentType> o2) {
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
	 * @see de.cesr.more.geo.building.MoreGeoNetworkBuilder#setGeography(repast.simphony.space.gis.Geography)
	 */
	@Override
	public void setGeography(Geography<Object> geography) {
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
}