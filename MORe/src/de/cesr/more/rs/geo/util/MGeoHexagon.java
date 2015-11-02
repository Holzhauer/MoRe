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
 * Created by Sascha Holzhauer on 21.06.2013
 */
package de.cesr.more.rs.geo.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import repast.simphony.query.space.gis.ContainsQuery;
import repast.simphony.space.gis.Geography;

import com.vividsolutions.jts.geom.Geometry;

import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.util.distributions.MRealDistribution;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * The class represents hexagons of the model region that are used to group agents in to clusters of near-by instances
 * to ease computation of distances between agents. It also holds an {@link ArrayList} of all agents within the
 * particular hexagon.
 *
 * @author Sascha Holzhauer
 * @date 21.06.2013
 *
 */
public class MGeoHexagon<AgentType> implements MoreGeoHexagon<AgentType> {

	public static class MGeoHexagonInitialiser<AgentType> implements MoreGeoHexagonInitialiser<AgentType> {

		/**
		 * @param pm
		 * @param geography
		 */
		@SuppressWarnings("unchecked")
		// type erasure for Class objects
		public void init(PmParameterManager pm, Geography<Object> geography) {
			// File hexagonShapeFile = new File((String) pm.getParam(MNetBuildHdffPa.HEXAGON_SHAPEFILE));
			// hexagonShapeFile.setReadOnly();
			//
			// // check if shapefile exists:
			// if (!hexagonShapeFile.exists()) {
			// logger.error("The specified shape file (" + hexagonShapeFile + ") does not exist!");
			// throw new IllegalArgumentException("The specified shape file (" + hexagonShapeFile
			// + ") does not exist!");
			// }
			//
			// // <- LOGGING
			// logger.info("Init hexagons from " + hexagonShapeFile);
			// // LOGGING ->
			//
			// MShapefileLoader<MGeoHexagon<AgentType>> areasLoader = null;
			//
			// double distanceFactorForDistribution = ((Double) pm.getParam(
			// MNetBuildHdffPa.DISTANCE_FACTOR_FOR_DISTRIBUTION)).doubleValue();
			//
			// try {
			// areasLoader = new MShapefileLoader<MGeoHexagon<AgentType>>(
			// (Class<MGeoHexagon<AgentType>>) (Class<?>) MGeoHexagon.class,
			// hexagonShapeFile.toURI().toURL(),
			// geography);
			// while (areasLoader.hasNext()) {
			// MGeoHexagon<AgentType> hexagon = new MGeoHexagon<AgentType>();
			// areasLoader.next(hexagon);
			// hexagon.distanceFactorForDistribution = distanceFactorForDistribution;
			// }
			// } catch (java.net.MalformedURLException e) {
			// logger.error("AreasCreator: malformed URL exception when reading areas shapefile.");
			// e.printStackTrace();
			// }
		}

		/**
		 * @see de.cesr.more.rs.geo.util.MoreGeoHexagonInitialiser#initDistanceMatrix(java.util.Collection, java.util.Map)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void initDistanceMatrix(Map<AgentType, MoreGeoHexagon<AgentType>> agentHexagons,
				Geography<Object> geography) {
			// <- LOGGING
			logger.info("Init distance matrix...");
			// LOGGING ->

			Set<MoreGeoHexagon<AgentType>> hexagons = new HashSet<MoreGeoHexagon<AgentType>>();
			for (Object o : geography.getLayer(MGeoHexagon.class).getAgentSet()) {
				hexagons.add((MoreGeoHexagon<AgentType>) o);
			}

			// height is required to determine distance ranges
			double halfWidth = geography.getGeometry(hexagons.iterator().next()).getEnvelopeInternal()
					.getWidth();

			// Use the geography's set of hexagons because elements get removed from hexagons in the loop:
			for (Object o : geography.getLayer(MGeoHexagon.class).getAgentSet()) {
				MGeoHexagon<AgentType> hexagon = (MGeoHexagon<AgentType>)o;
				hexagon.setHexagonWidth(halfWidth);
				Geometry hexagonGeo = geography.getGeometry(hexagon);
				Geometry hexagonCentroid = geography.getGeometry(hexagon).getCentroid();

				//<- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Hexagons (" + hexagon + ") centroid: " + hexagonCentroid);
				}
				// LOGGING ->

				// Assign Agents to hexagons
				ContainsQuery<Object> containsQuery = new ContainsQuery<Object>(
						geography, hexagonGeo);
				for (Object a : containsQuery.query()) {
					// Search agents within hexagon and init map agent > hexagon
					if (a instanceof MoreMilieuAgent) {
						agentHexagons.put((AgentType) a, hexagon);
						hexagon.addAgent((AgentType) a);

						// <- LOGGING
						if (logger.isDebugEnabled()) {
							logger.debug("Added agent " + a + " to hexagon " + hexagon);
						}
						// LOGGING ->
					}
				}
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
		}

		/**
		 * @see de.cesr.more.rs.geo.util.MoreGeoHexagonInitialiser#getHexagonType()
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Class<? extends MoreGeoHexagon> getHexagonType() {
			return MGeoHexagon.class;
		}
	}

	/**
	 * Logger
	 */
	static private Logger								logger			= Logger.getLogger(MGeoHexagon.class);

	/**
	 * The value may not fall below twice the largest distance from the centroid to any point on the edge (otherwise, 
	 * a lower limit may exceed an upper limit which leads to errors from the treeset; if the value is too
	 * large too many hexagons are considered). 
	 */
	protected double									hexagonHalfWidth	= 0.0;

	protected static int								idCounter		= 1;

	protected int										id;
	protected ArrayList<AgentType>						agents			= new ArrayList<AgentType>();

	protected Map<MoreGeoHexagon<AgentType>, Double>	hexagonDistance	= new HashMap<MoreGeoHexagon<AgentType>, Double>();
	protected TreeSet<Distance>							distanceHexagon	= new TreeSet<Distance>();
	
	protected double									distanceFactorForDistribution;

	protected class Distance implements Comparable<Distance> {
		double						distance;
		MoreGeoHexagon<AgentType>	hexagon;

		protected Distance(Double distance, MoreGeoHexagon<AgentType> hexagon) {
			this.distance = distance;
			this.hexagon = hexagon;
		}

		@Override
		public int compareTo(Distance other) {
			return this.distance < other.distance ? -1 :
					this.distance > other.distance ? 1 : (this.hexagon != null && other.hexagon != null) ?
							this.hexagon.compareTo(other.hexagon) : -1;
		}

		public String toString() {
			return "Distance(" + this.distance + " / " + this.hexagon + ")";
		}
	}

	/**
	 * The hexagon height is required to determine the distances
	 *
	 * @param width
	 */
	public void setHexagonWidth(double width) {
		hexagonHalfWidth = width / 2.0;
	}

	/**
	 * Assign unique id.
	 */
	public MGeoHexagon() {
		id = idCounter++;
	}

	public int getId() {
		return this.id;
	}

	/**
	 * @param agent
	 *        within the hexagon
	 */
	public void addAgent(AgentType agent) {
		agents.add(agent);
	}

	/**
	 * @param agent
	 */
	public void removeAgent(AgentType agent) {
		agents.remove(agent);
	}

	/**
	 * @return list of agents within this hexagon
	 */
	public ArrayList<AgentType> getAgents() {
		return agents;
	}

	/**
	 * @param hexagon
	 * @param distance
	 */
	public void setDistance(MoreGeoHexagon<AgentType> hexagon, Double distance) {
		this.hexagonDistance.put(hexagon, distance);
		this.distanceHexagon.add(new Distance(distance, hexagon));
	}

	/**
	 * @see de.cesr.more.rs.geo.util.MoreGeoHexagon#getDistance(de.cesr.more.rs.geo.util.MoreGeoHexagon)
	 */
	public double getDistance(MoreGeoHexagon<AgentType> hexagon) {
		return this.hexagonDistance.get(hexagon).doubleValue();
	}

	/**
	 * @see de.cesr.more.rs.geo.util.MoreGeoHexagon#getHexagonsOfDistance(java.lang.Object, double)
	 */
	public Set<MoreGeoHexagon<AgentType>> getHexagonsOfDistance(AgentType agent, MRealDistribution distribution) {
		double distance = Double.MAX_VALUE;
		
		Distance lower, upper;
		do {
			do {
				distance = distribution.sample() * this.distanceFactorForDistribution;
			} while (distance - hexagonHalfWidth > distanceHexagon.last().distance);
			
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Start-Distance: " + distance);
			}
			// LOGGING ->
			
			
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				StringBuffer buffer = new StringBuffer();
				
				for (Distance d : distanceHexagon) {
					buffer.append(d.hexagon + "(" + d.distance + "), ");
				}
				logger.debug("Distance hexagon map: " + buffer.toString());
			}
			// LOGGING ->
			
			// to capture agents of the given distances, hexagons need to be considered whose centroid
			// is +/- (hexagonHeight/2.0) away (if we assume that the agents coordinates can deviate from
			// its hexagons centroid by (hexagonHeight/2.0) we would need to apply +/- hexagonHeight).
			lower = distanceHexagon.higher(new Distance(distance - hexagonHalfWidth, null));
			upper = distanceHexagon.lower(new Distance(distance + hexagonHalfWidth, null));
			
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Lower hexagon: " + lower);
				logger.debug("Upper hexagon: " + upper);
			}
			// LOGGING ->
			
			// Actually, these cases should not occur (tested above):
			// if lower is null, all hexagons are lower than the requested, thus taking the last.
			lower = lower != null ? lower : distanceHexagon.first();
			// if upper is null, all hexagons are higher than the requested, thus taking the first.
			upper = upper != null ? upper : distanceHexagon.last();
			
			// In case hexagons are isolated, sample distance again:
		} while (lower.distance > upper.distance);
			
		Set<MoreGeoHexagon<AgentType>> hexagons = new LinkedHashSet<MoreGeoHexagon<AgentType>>();

		// Query the subset
		if (lower == upper) {
			hexagons.add(lower.hexagon);
		} else {
			for (Distance d : distanceHexagon.subSet(lower, upper)) {
				hexagons.add(d.hexagon);
			}
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Number of hexagons retrieved: " + hexagons.size());
		}
		// LOGGING ->

		return hexagons;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (o instanceof MGeoHexagon) {
			return this.id == ((MGeoHexagon<AgentType>) o).getId();
		} else {
			return false;
		}
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MoreGeoHexagon<AgentType> hexagon) {
		return this.id < hexagon.getId() ? -1 :
				(this.id > hexagon.getId() ? 1 : 0);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Hexagon_" + this.id;
	}
}