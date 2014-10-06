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
 * Created by sholzhau on 13 Jun 2014
 */
package de.cesr.more.rs.geo.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import repast.simphony.query.space.gis.WithinQuery;
import repast.simphony.space.gis.Geography;

import com.vividsolutions.jts.geom.Geometry;

import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.util.distributions.MRealDistribution;
import de.cesr.more.util.io.MShapefileLoader;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author sholzhau
 * @date 13 Jun 2014
 *
 */
public class MGeoHexagon1stLayer<AgentType> extends MGeoHexagon<AgentType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MGeoHexagon1stLayer.class);

	public static class MGeoHexagonInitialiser<AgentType> implements MoreGeoHexagonInitialiser<AgentType> {
		
		MGeoHexagon2ndLayer.MGeoHexagonInitialiser<AgentType> secondHexInitialiser = null;

		/**
		 * @param pm
		 * @param geography
		 */
		@SuppressWarnings("unchecked")
		// type erasure for Class objects
		public void init(PmParameterManager pm, Geography<Object> geography) {
			File hexagonShapeFile = new File((String) pm.getParam(MNetBuildHdffPa.HEXAGON_SHAPEFILE));

			// check if shapefile exists:
			if (!hexagonShapeFile.exists()) {
				logger.error("The specified shape file (" + hexagonShapeFile + ") does not exist!");
				throw new IllegalArgumentException("The specified shape file (" + hexagonShapeFile
						+ ") does not exist!");
			}

			// <- LOGGING
			logger.info("Init hexagons from " + hexagonShapeFile);
			// LOGGING ->

			MShapefileLoader<MGeoHexagon<AgentType>> areasLoader = null;

			try {
				areasLoader = new MShapefileLoader<MGeoHexagon<AgentType>>(
						(Class<MGeoHexagon<AgentType>>) (Class<?>) MGeoHexagon1stLayer.class,
						hexagonShapeFile.toURI().toURL(),
						geography);
				while (areasLoader.hasNext()) {
					areasLoader.next(new MGeoHexagon1stLayer<AgentType>());
				}
			} catch (java.net.MalformedURLException e) {
				logger.error("AreasCreator: malformed URL exception when reading areas shapefile.");
				e.printStackTrace();
			}

			this.secondHexInitialiser = new MGeoHexagon2ndLayer.MGeoHexagonInitialiser<AgentType>();
			this.secondHexInitialiser.init(pm, geography);
		}

		/**
		 * @see de.cesr.more.rs.geo.util.MoreGeoHexagonInitialiser#initDistanceMatrix(java.util.Collection, java.util.Map)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void initDistanceMatrix(Map<AgentType, MoreGeoHexagon<AgentType>> agentHexagons,
				Geography<Object> geography) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Init distance matrix...");
			}
			// LOGGING ->

			double hexagonWidth = geography.getGeometry(geography.getLayer(MGeoHexagon1stLayer.class).
					getAgentSet().iterator().next()).getEnvelopeInternal().getWidth();

			Set<MoreGeoHexagon<AgentType>> hexagons = new HashSet<MoreGeoHexagon<AgentType>>();
			for (Object o : geography.getLayer(MGeoHexagon1stLayer.class).getAgentSet()) {
				hexagons.add((MoreGeoHexagon<AgentType>) o);
			}


			// Use the geography's set of hexagons because elements get removed from hexagons in the loop:
			for (Object o : geography.getLayer(MGeoHexagon1stLayer.class).getAgentSet()) {
				MGeoHexagon1stLayer<AgentType> hexagon = (MGeoHexagon1stLayer<AgentType>)o;
				hexagon.setHexagonWidth(hexagonWidth);
				Geometry hexagonCentroid = geography.getGeometry(hexagon).getCentroid();

				hexagon.geography = geography;

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Hexagons centroid: " + hexagonCentroid);
				}
				// LOGGING ->

				// For each remaining hexagon p_rest
				for (MoreGeoHexagon<AgentType> h : hexagons) {
					// Determine distance between hexagon and p_rest
					double distance = hexagonCentroid.distance(geography.getGeometry(h).getCentroid());
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Distance between " + hexagon + " and " + h + ": " + distance);
					}
					// LOGGING ->

					hexagon.setDistance(h, distance);
					h.setDistance(hexagon, distance);
				}
				hexagons.remove(hexagon);
			}
			this.secondHexInitialiser.initDistanceMatrix(agentHexagons, geography);
			for (Object o : geography.getLayer(MGeoHexagon1stLayer.class).getAgentSet()) {
				((MGeoHexagon1stLayer<AgentType>) o).initAgentMap();
			}
		}

		/**
		 * @see de.cesr.more.rs.geo.util.MoreGeoHexagonInitialiser#getHxagonType()
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Class<? extends MoreGeoHexagon> getHexagonType() {
			return MGeoHexagon1stLayer.class;
		}
	}

	protected boolean agentMapInitialised = false;

	protected Geography<Object> geography = null;

	protected Map<AgentType, MGeoHexagon2ndLayer<AgentType>> agent2Hexagon2ndLayer =
				new HashMap<AgentType, MGeoHexagon2ndLayer<AgentType>>();

	protected Set<MGeoHexagon2ndLayer<AgentType>> hexagons2ndLayer = new LinkedHashSet<MGeoHexagon2ndLayer<AgentType>>();


	/**
	 * NOTE: This method needs to be called in case of restoring since it is required to identify the 2nd layer hexagon
	 * in case of removal (called in
	 * {@link MGeoHexagon1stLayer.MGeoHexagonInitialiser#initDistanceMatrix(Map, Geography)}).
	 */
	protected void initAgentMap() {
		if (!this.agentMapInitialised) {
			for (MGeoHexagon2ndLayer<AgentType> h2 : hexagons2ndLayer) {
				for (AgentType a : h2.getAgents()) {
					this.agent2Hexagon2ndLayer.put(a, h2);
				}
			}
			this.agentMapInitialised = true;
		}
	}

	/**
	 * @see de.cesr.more.rs.geo.util.MGeoHexagon#addAgent(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void addAgent(AgentType agent) {
		WithinQuery<Object> query = new WithinQuery<Object>(geography, agent);
		for (Object o : query.query()) {
			if (o instanceof MGeoHexagon2ndLayer) {
				((MGeoHexagon2ndLayer<AgentType>)o).addAgent(agent);
				agent2Hexagon2ndLayer.put(agent, (MGeoHexagon2ndLayer<AgentType>) o);
			}
		}
	}

	/**
	 * @see de.cesr.more.rs.geo.util.MGeoHexagon#removeAgent(java.lang.Object)
	 */
	public void removeAgent(AgentType agent) {
		agent2Hexagon2ndLayer.get(agent).removeAgent(agent);
		agent2Hexagon2ndLayer.remove(agent);
	}

	public void add2ndLayerHexagon(MGeoHexagon2ndLayer<AgentType> hexagon) {
		this.hexagons2ndLayer.add(hexagon);
	}

	/**
	 * @see de.cesr.more.rs.geo.util.MGeoHexagon#getAgents()
	 */
	public ArrayList<AgentType> getAgents() {
		ArrayList<AgentType> agents = new ArrayList<AgentType>();
		for (MGeoHexagon2ndLayer<AgentType> h : hexagons2ndLayer) {
			agents.addAll(h.getAgents());
		}
		return agents;
	}

	public Collection<MGeoHexagon2ndLayer<AgentType>> get2ndLayerHexagons() {
		return this.hexagons2ndLayer;
	}

	/**
	 * @see de.cesr.more.rs.geo.util.MGeoHexagon#getHexagonsOfDistance(java.lang.Object, double)
	 */
	public Set<MoreGeoHexagon<AgentType>> getHexagonsOfDistance(AgentType agent, MRealDistribution distanceDistribution) {
		initAgentMap();
		return agent2Hexagon2ndLayer.get(agent).getHexagonsOfDistance(agent, distanceDistribution);
	}
}
