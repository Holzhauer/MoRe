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

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.cesr.more.util.Log4jLogger;
import de.cesr.more.building.MDefaultEdgeFactory;
import de.cesr.more.building.MoreEdgeFactory;
import de.cesr.more.building.MoreGeoNetworkBuilder;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.networks.MoreRsNetwork;
import de.cesr.more.rs.adapter.MRepastEdge;
import de.cesr.more.util.MoreUtilities;
import de.cesr.more.util.param.MNetworkBuildingParam;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author holzhauer
 * @date 23.09.2011 
 *
 */
public abstract class MGeoRsNetworkService<AgentType, EdgeType extends MRepastEdge<AgentType>> implements
		MoreRsNetworkService<AgentType, EdgeType>,
		MoreGeoNetworkBuilder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger			logger			= Log4jLogger.getLogger(MGeoRsNetworkService.class);

	private static final double		ARROW_ARC		= 20.0 / 180.0;
	/**
	 * in degree
	 */
	private static final double		ARROW_LENGTH	= 0.0005;

	
	/**
	 * Need to be of type {@link Object} since network objects and agents should be insertable
	 */
	protected Geography<Object>		areasGeography;
	
	protected GeometryFactory		geoFactory		= null;

	/**
	 * The context the network belongs to.
	 */
	protected Context<?>						   context;

	protected MoreEdgeFactory<AgentType, EdgeType> edgeFactory;
	
	/**
	 * @param areasGeography
	 */
	public MGeoRsNetworkService(Geography<Object> areasGeography, MoreEdgeFactory<AgentType, EdgeType> edgeFactory) {
		this.areasGeography = areasGeography;
		this.edgeFactory = edgeFactory;
		this.geoFactory = new GeometryFactory(new PrecisionModel(),
				((Integer) PmParameterManager.getParameter(MNetworkBuildingParam.SPATIAL_REFERENCE_ID)).intValue());
	}

	/**
	 * @param areasGeography
	 */
	public MGeoRsNetworkService(Geography<Object> areasGeography) {
		this(areasGeography, new MDefaultEdgeFactory<AgentType, EdgeType>());
	}
	
	/**
	 * Set the (root) context the network shall span
	 * 
	 * @param context
	 */
	public void setContext(Context<?> context) {
		this.context = context;
	}

	/**
	 * @param agents the agents that are to be connected by the network builder
	 * @param name the network's name
	 * 
	 * @return a network
	 */
	public abstract MoreRsNetwork<AgentType, EdgeType> buildRsNetwork(Collection<AgentType> agents, String name);

	/**
	 * @see de.cesr.more.building.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {
		return buildRsNetwork(agents, "Network");
	}

	/**
	 * Created an edge in the direction from potInfluencer to influencedHh
	 * 
	 * @param network
	 * @param influencedHh
	 * @param potInfluencer
	 */
	protected void createEdge(MoreNetwork<AgentType, EdgeType> network, AgentType influencedHh,
			AgentType potInfluencer) {
		EdgeType edge = edgeFactory.createEdge(potInfluencer, influencedHh, true);

		network.connect(edge);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Edge added: " + edge);
		}
		// LOGGING ->

		addEdgeToGeography(influencedHh, potInfluencer, edge);
	}

	/**
	 * @param hh
	 * @param potInfluencer
	 * @param edge Created by Sascha Holzhauer on 25.01.2011
	 */
	protected void addEdgeToGeography(AgentType hh, AgentType potInfluencer, EdgeType edge) {
		if ((Boolean) PmParameterManager.getParameter(MNetworkBuildingParam.ADD_EDGES_TO_GEOGRAPHY)) {
			Coordinate[] coords1 = { areasGeography.getGeometry(hh).getCoordinate(),
					areasGeography.getGeometry(potInfluencer).getCoordinate() };

			// calculate arc location

			Coordinate hhCoord = areasGeography.getGeometry(hh).getCoordinate();
			Coordinate influencerCoord = areasGeography.getGeometry(potInfluencer).getCoordinate();
			double arcArrow = Math.atan((influencerCoord.y - hhCoord.y) / (influencerCoord.x - hhCoord.x));

			Coordinate[] coordsE = {
					hhCoord,
					new Coordinate(hhCoord.x - ARROW_LENGTH * Math.cos(arcArrow), hhCoord.y + ARROW_LENGTH
							* Math.sin(arcArrow)) };

			Coordinate[] coordsL = {
					hhCoord,
					new Coordinate(hhCoord.x - ARROW_LENGTH * Math.cos(1.0 / 2.0 - ARROW_ARC - arcArrow), hhCoord.y
							- ARROW_LENGTH * Math.sin(90.0 - ARROW_ARC - arcArrow)) };
			Coordinate[] coordsR = {
					hhCoord,
					new Coordinate(hhCoord.x - ARROW_LENGTH * Math.cos(1.0 / 2.0 + ARROW_ARC - arcArrow), hhCoord.y
							- ARROW_LENGTH * Math.sin(90.0 + ARROW_ARC - arcArrow)) };

			LineString[] lines = { this.geoFactory.createLineString(coords1),
			// TODO finish
			// ModelManager.getMan().getGeomFactory().createLineString(coordsE),
			// ModelManager.getMan().getGeomFactory().createLineString(coordsR)
			};

			areasGeography.move(edge, this.geoFactory.createMultiLineString(lines));

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Moved " + edge + " to " + lines + "(" + coordsL[0].x + "," + coordsL[0].y + ")" + " / "
						+ "(" + coordsL[1].x + "," + coordsL[1].y + ")" + "-" + "(" + coordsR[0].x + "," + coordsR[0].y
						+ ")" + " / " + "(" + coordsR[1].x + "," + coordsR[1].y + ")");
			}
			// LOGGING ->
		}
	}

	/**
	 * @param network
	 */
	protected void outputNetwork(MoreNetwork<AgentType, EdgeType> network) {
		File file1 = new File(((String) PmParameterManager.getParameter(MNetworkBuildingParam.NETWORK_TARGET_FILE)));
		MoreUtilities.<AgentType, EdgeType> outputGraph(network, file1);
	}

	
	/*************************************
	 *   GETTER & SETTER
	 *************************************/

	/**
	 * @return the edgeFactory
	 */
	public MoreEdgeFactory<AgentType, EdgeType> getEdgeFactory() {
		return edgeFactory;
	}

	/**
	 * @param edgeFactory the edgeFactory to set
	 */
	public void setEdgeFactory(MoreEdgeFactory<AgentType, EdgeType> edgeFactory) {
		this.edgeFactory = edgeFactory;
	}
	
	/**
	 * @return the geoFactory
	 */
	public GeometryFactory getGeoFactory() {
		return geoFactory;
	}

	/**
	 * @param geoFactory the geoFactory to set
	 */
	public void setGeoFactory(GeometryFactory geoFactory) {
		this.geoFactory = geoFactory;
	}
}