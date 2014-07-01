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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import repast.simphony.query.space.gis.ContainsQuery;
import repast.simphony.query.space.gis.IntersectsQuery;
import repast.simphony.space.gis.Geography;

import com.vividsolutions.jts.geom.Geometry;

import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.util.io.MShapefileLoader;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author sholzhau
 * @date 13 Jun 2014
 *
 */
public class MGeoHexagon2ndLayer<AgentType> extends MGeoHexagon<AgentType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MGeoHexagon2ndLayer.class);

	public static class MGeoHexagonInitialiser<AgentType> implements MoreGeoHexagonInitialiser<AgentType> {

		/**
		 * @param pm
		 * @param geography
		 */
		@SuppressWarnings("unchecked")
		// type erasure for Class objects
		public void init(PmParameterManager pm, Geography<Object> geography) {
			File hexagonShapeFile = new File((String) pm.getParam(MNetBuildHdffPa.HEXAGON_SHAPEFILE_2ND));

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
						(Class<MGeoHexagon<AgentType>>) (Class<?>) MGeoHexagon2ndLayer.class,
						hexagonShapeFile.toURI().toURL(),
						geography);
				while (areasLoader.hasNext()) {
					areasLoader.next(new MGeoHexagon2ndLayer<AgentType>(((Boolean)pm.getParam(MNetBuildHdffPa.INCREASED_DISTANCE_ACCURACY)).booleanValue()));
				}
			} catch (java.net.MalformedURLException e) {
				logger.error("AreasCreator: malformed URL exception when reading areas shapefile.");
				e.printStackTrace();
			}
		}

		/**
		 * @see de.cesr.more.rs.geo.util.MoreGeoHexagonInitialiser#initDistanceMatrix(java.util.Collection, java.util.Map, repast.simphony.space.gis.Geography)
		 */
		public void initDistanceMatrix(Collection<AgentType> agents,
				Map<AgentType, MoreGeoHexagon<AgentType>> agentHexagons, Geography<Object> geography) {
			// height is required to determine distance ranges
			MGeoHexagon2ndLayer.setHexagonWidth(geography.getGeometry(geography.getLayer(MGeoHexagon2ndLayer.class).
					getAgentSet().iterator().next()).getEnvelopeInternal().getWidth());

			for (Object o : geography.getLayer(MGeoHexagon2ndLayer.class).getAgentSet()) {
				@SuppressWarnings("unchecked")
				MGeoHexagon2ndLayer<AgentType> hexagon = (MGeoHexagon2ndLayer<AgentType>)o;

				hexagon.geography = geography;

				// Assign 2ndLayerHexagons to 1stLayerHexagons
				Geometry secondHexagonGeo = geography.getGeometry(hexagon);
				
				// make sure to add the 2nd layer hexagon to that 1st layer hexagon which covers
				// most of its area (and no other one else):
				IntersectsQuery<Object> intersectsQuery = new IntersectsQuery<Object>(
						geography, secondHexagonGeo);
				double intersection = 0.0;
				MGeoHexagon1stLayer<AgentType> maximum = null;
				
				for (Object i : intersectsQuery.query()) {
					if (i instanceof MGeoHexagon1stLayer) {
						double area = geography.getGeometry(i).difference(secondHexagonGeo).getArea();
						if (area > intersection) {
							intersection = area;
							@SuppressWarnings("unchecked")
							MGeoHexagon1stLayer<AgentType> hex = (MGeoHexagon1stLayer<AgentType>) i;
							maximum = hex; 
						}
					}
				}
				if (maximum != null) {
					maximum.add2ndLayerHexagon(hexagon);
				}
				
				// add agents to 2nd layer hexagons:
				ContainsQuery<Object> containsQuery = new ContainsQuery<Object>(
						geography, geography.getGeometry(hexagon));

				for (Object a : containsQuery.query()) {
					// Search agents within hexagon and assign agent to hexagon
					if (a instanceof MoreMilieuAgent) {
						@SuppressWarnings("unchecked")
						AgentType agent = (AgentType) a;
						hexagon.addAgent(agent);

						agentHexagons.put(agent, maximum);
						
						// <- LOGGING
						if (logger.isDebugEnabled()) {
							logger.debug("Added agent " + a + " to hexagon " + hexagon);
						}
						// LOGGING ->
					}
				}
			}
		}

		/**
		 * @see de.cesr.more.rs.geo.util.MoreGeoHexagonInitialiser#getHexagonType()
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Class<? extends MoreGeoHexagon> getHexagonType() {
			return MGeoHexagon2ndLayer.class;
		}
	}
	
	/**
	 * The value may not fall below the largest distance from the centroid to any point on the edge (otherwise, 
	 * a lower limit may exceed an upper limit which leads to errors from the treeset; if the values is too
	 * large too many hexagons are considered). 
	 */
	static protected double								hexagonWidth2nd	= 0.0;
	
	/**
	 * The hexagon height is required to determine the distances
	 *
	 * @param height
	 */
	static public void setHexagonWidth(double height) {
		hexagonWidth2nd = height;
	}

	protected Geography<Object> geography = null;

	protected boolean distancesInitialised = false;

	protected boolean increasedDistanceAccuracy = true;

	public MGeoHexagon2ndLayer(boolean increasedDisanceAccuracy) {
		this.increasedDistanceAccuracy = increasedDisanceAccuracy;
	}

	protected void initDistanceMatrix(Collection<AgentType> agents,
			Map<AgentType, MoreGeoHexagon<AgentType>> agentHexagons, Geography<Object> geography) {

		if (!distancesInitialised) {
			// <- LOGGING
			logger.info(this + "> Init distance matrix...");
			if (logger.isDebugEnabled()) {
				for (AgentType agent : agents) {
					logger.debug(agent + "> centroid: " + geography.getGeometry(agent).getCentroid());
				}
			}
			// LOGGING ->


			Geometry thisHexagonGeo = geography.getGeometry(this);
			Geometry thisHexagonCentroid = thisHexagonGeo.getCentroid();

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Hexagons centroid: " + thisHexagonCentroid);
			}
			// LOGGING ->

			for (Object o : geography.getLayer(MGeoHexagon1stLayer.class).getAgentSet()) {
				@SuppressWarnings("unchecked")
				MGeoHexagon1stLayer<AgentType> hexagon = (MGeoHexagon1stLayer<AgentType>)o;

				double distance = thisHexagonCentroid.distance(geography.getGeometry(hexagon).getCentroid());

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Distance between " + hexagon + " and " + thisHexagonGeo + ": " + distance);
				}
				// LOGGING ->

				this.setDistance(hexagon, distance);
			}
		}
		this.distancesInitialised = true;
	}

	/**
	 * @see de.cesr.more.rs.geo.util.MoreGeoHexagon#getDistance(de.cesr.more.rs.geo.util.MoreGeoHexagon)
	 */
	public double getDistance(MoreGeoHexagon<AgentType> hexagon) {
		// init hexagon:
		if (!distancesInitialised) {
			this.initDistanceMatrix(agents, null, geography);
		}
		return this.hexagonDistance.get(hexagon).doubleValue();
	}

	public boolean overlapsWithRadius(AgentType agent, double distance) {
		return geography.getGeometry(agent).distance(geography.getGeometry(this).getCentroid()) - distance <=
				hexagonWidth2nd / 2.0;
	}

	/**
	 * @see de.cesr.more.rs.geo.util.MGeoHexagon#getHexagonsOfDistance(java.lang.Object, double)
	 */
	public Set<MoreGeoHexagon<AgentType>> getHexagonsOfDistance(AgentType agent, double distance) {
		if (!distancesInitialised) {
			this.initDistanceMatrix(agents, null, geography);
		}

		Set<MoreGeoHexagon<AgentType>> hexagons = new LinkedHashSet<MoreGeoHexagon<AgentType>>();

		Distance lower = distanceHexagon.higher(new Distance(distance - (hexagonWidth / 2.0), null)); 
		Distance upper = distanceHexagon.lower(new Distance(distance + (hexagonWidth / 2.0), null));

		lower = lower != null ? lower : distanceHexagon.first();
		upper = upper != null ? upper : distanceHexagon.last();
		
		// Query the subset
		if (lower == upper) {
			if (this.increasedDistanceAccuracy && geography.getGeometry(agent).distance(geography .getGeometry(lower.hexagon).getCentroid()) - distance <=
					hexagonWidth / 2.0) {
				// check 2nd layer hexagons of lower.hexagon
				for (MGeoHexagon2ndLayer<AgentType> h2 : ((MGeoHexagon1stLayer<AgentType>)lower.hexagon).get2ndLayerHexagons()) {
					if (h2.overlapsWithRadius(agent, distance)) {
						hexagons.add(h2);
					}
				}
			}
		} else {
			for (Distance d : distanceHexagon.subSet(lower, upper)) {
				if (this.increasedDistanceAccuracy && geography.getGeometry(agent).distance(geography.getGeometry(d.hexagon).getCentroid()) - distance <=
						hexagonWidth ) {
					// check 2nd layer hexagons of d.hexagon
					for (MGeoHexagon2ndLayer<AgentType> h2 : ((MGeoHexagon1stLayer<AgentType>)d.hexagon).get2ndLayerHexagons()) {
						if (h2.overlapsWithRadius(agent, distance)) {
							hexagons.add(h2);
						}
					}
				}
			}
		}
		return hexagons;
	}
}
