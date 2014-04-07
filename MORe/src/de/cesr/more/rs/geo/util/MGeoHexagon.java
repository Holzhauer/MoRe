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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;


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
public class MGeoHexagon<AgentType> implements Comparable<MGeoHexagon<AgentType>> {

	/**
	 * Logger
	 */
	static private Logger							logger			= Logger.getLogger(MGeoHexagon.class);

	static protected double							hexagonHeight	= 0.0;

	protected static int							idCounter		= 1;

	protected int									id;
	protected ArrayList<AgentType>					agents			= new ArrayList<AgentType>();

	protected Map<MGeoHexagon<AgentType>, Double>	hexagonDistance	= new HashMap<MGeoHexagon<AgentType>, Double>();
	protected TreeSet<Distance>						distanceHexagon	= new TreeSet<Distance>();

	protected class Distance implements Comparable<Distance> {
		double					distance;
		MGeoHexagon<AgentType>	hexagon;

		protected Distance(Double distance, MGeoHexagon<AgentType> hexagon) {
			this.distance = distance;
			this.hexagon = hexagon;
		}

		@Override
		public int compareTo(Distance other) {
			return this.distance < other.distance ? -1 :
					this.distance > other.distance ? 1 : (this.hexagon != null && other.hexagon != null) ?
							this.hexagon.compareTo(other.hexagon) : -1;
		}
	}

	/**
	 * The hexagon height is required to determine the distances
	 * 
	 * @param height
	 */
	static public void setHexagonHeight(double height) {
		hexagonHeight = height;
	}

	/**
	 * Assign unique id.
	 */
	public MGeoHexagon() {
		id = idCounter++;
	}

	/**
	 * @return hexagon's ID
	 */
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
	public void setDistance(MGeoHexagon<AgentType> hexagon, Double distance) {
		this.hexagonDistance.put(hexagon, distance);
		this.distanceHexagon.add(new Distance(distance, hexagon));
	}

	/**
	 * @param hexagon
	 * @return
	 */
	public double getDistance(MGeoHexagon<AgentType> hexagon) {
		return this.hexagonDistance.get(hexagon).doubleValue();
	}

	public Set<MGeoHexagon<AgentType>> getHexagonsOfDistance(double distance) {
		Set<MGeoHexagon<AgentType>> hexagons = new LinkedHashSet<MGeoHexagon<AgentType>>();
		// to capture agents of the given distances, hexagons need to be considered whose centroid
		// is +/- (hexagonHeight/2.0) away (if we assume that the agents coordinates can deviate from
		// its hexagons centroid by (hexagonHeight/2.0) we would need to apply +/- hexagonHeight).
		Distance lower = distanceHexagon.higher(new Distance(distance - (hexagonHeight / 2.0), null));
		Distance upper = distanceHexagon.lower(new Distance(distance + (hexagonHeight / 2.0), null));
		// Query the subset
		if (lower == upper) {
			hexagons.add(lower.hexagon);
		} else {
			for (Distance d : distanceHexagon.subSet(
					lower != null ? lower : distanceHexagon.first(),
					upper != null ? upper : distanceHexagon.last())) {
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
	public int compareTo(MGeoHexagon<AgentType> hexagon) {
		return this.id < hexagon.id ? -1 :
				(this.id > hexagon.id ? 1 : 0);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Hexagon_" + this.id;
	}
}