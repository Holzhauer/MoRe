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
package de.cesr.more.geo.building.network;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import repast.simphony.space.gis.Geography;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MNetworkService;
import de.cesr.more.geo.MoreGeoEdge;
import de.cesr.more.geo.building.edge.MGeoNetworkEdgeModifier;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.util.Log4jLogger;
import de.cesr.parma.core.PmParameterManager;

/**
 * Removal and Addition of nodes and agents to geo-referenced networks.
 *
 * @author holzhauer
 * @date 23.09.2011 
 *
 */
public abstract class MGeoNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MoreGeoEdge<AgentType> & MoreEdge<AgentType>>
		extends MNetworkService<AgentType, EdgeType> implements MoreGeoNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger					logger			= Log4jLogger.getLogger(MGeoNetworkService.class);

	
	/**
	 * Need to be of type {@link Object} since network objects and agents should be insertable
	 */
	protected Geography<Object>		geography;
	
	protected GeometryFactory		geoFactory		= null;
	
	protected Class<? extends AgentType>	geoRequestClass	= null;


	/**
	 * @param areasGeography
	 */
	public MGeoNetworkService(Geography<Object> areasGeography, MoreEdgeFactory<AgentType, EdgeType> edgeFac,
			PmParameterManager pm) {
		super(edgeFac);
		this.pm = pm;
		this.geography = areasGeography;
		this.geoFactory = new GeometryFactory(new PrecisionModel(),
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.SPATIAL_REFERENCE_ID)).intValue());
		this.edgeModifier = new MGeoNetworkEdgeModifier<AgentType, EdgeType>(edgeFac);

		// <- LOGGING
		logger.info("Initialised " + this + " with edge factory " + edgeFac);
		// LOGGING ->
	}

	/**
	 * @param areasGeography
	 */
	public MGeoNetworkService(Geography<Object> areasGeography, MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(areasGeography, edgeFac, PmParameterManager.getInstance(null));
	}

	/**
	 * @param areasGeography
	 */
	@SuppressWarnings("unchecked")
	public MGeoNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac,
				PmParameterManager.getInstance(null));
	}
	
	/**
	 * @param areasGeography
	 */
	@SuppressWarnings("unchecked") // risky but not avoidable
	@Deprecated
	public MGeoNetworkService() {
		this(null, (MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>(),
				PmParameterManager.getInstance(null));
	}


	/**
	 * Specify super type method for MilieuAgents
	 * @param network
	 */
	@Override
	protected void logEdges(Logger logger, MoreNetwork<AgentType, EdgeType> network, String prestring) {
		if (logger.isDebugEnabled()) {
			Set<MoreEdge<AgentType>> edges = new TreeSet<MoreEdge<AgentType>>(
					new Comparator<MoreEdge<AgentType>>() {

						@Override
						public int compare(MoreEdge<AgentType> o1,
								MoreEdge<AgentType> o2) {
							if (!o1.getStart().getAgentId()
									.equals(o2.getStart().getAgentId())) {
								return o1.getStart().getAgentId()
										.compareTo(o2.getStart().getAgentId());
							} else {
								return o1.getEnd().getAgentId()
										.compareTo(o2.getEnd().getAgentId());
							}
						}
					});
			edges.addAll(network.getEdgesCollection());
			for (MoreEdge<AgentType> edge : edges) {
				logger.debug(prestring + edge);
			}
		}
	}
	
	/**
	 * @param neighbourslist
	 */
	protected void shuffleCollection(List<AgentType> neighbourslist) {
		try {
			Collections.<AgentType> sort(neighbourslist,
				new Comparator<AgentType>() {
					@Override
					public int compare(AgentType o1, AgentType o2) {
						return o1.getAgentId().compareTo(o2.getAgentId());
					}
				});
		} catch (ClassCastException exception) {
			// <- LOGGING
			logger.error("It seems that the list of potential neighbours contains objects that are not agents. e.g. an edge object. This is "
					+ "the case when the agent class has no particular agent super class. " +
							"Use <networkBuilder>.setGeoRequestClass() to set the proper agent class!");
			// LOGGING ->
		}
		Collections.shuffle(neighbourslist, new Random(
				((Integer) pm.getParam(MRandomPa.RANDOM_SEED_NETWORK_BUILDING)).intValue()));
	}
	
	/*************************************
	 *   GETTER & SETTER
	 *************************************/
	
	/**
	 * @see de.cesr.more.geo.building.network.MoreGeoNetworkBuilder#setGeography(repast.simphony.space.gis.Geography)
	 */
	@Override
	public void setGeography(Geography<Object> geography) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set geography: " + geography);
		}
		// LOGGING ->

		this.geography = geography;
		this.edgeModifier = new MGeoNetworkEdgeModifier<AgentType, EdgeType>(this.edgeFac, geography, geoFactory);
	}

	/**
	 * @see de.cesr.more.geo.building.network.MoreGeoNetworkBuilder#getGeography()
	 */
	public Geography<Object> getGeography() {
		return this.geography;
	}

	/**
	 * @return the geoRequestClass
	 */
	public Class<? extends AgentType> getGeoRequestClass() {
		return geoRequestClass;
	}


	@Override
	public void setGeoRequestClass(Class<? extends AgentType> geoRequestClass) {
		this.geoRequestClass = geoRequestClass;
	}
}