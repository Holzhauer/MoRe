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
 * Created by Sascha Holzhauer on 22.07.2010
 */
package de.cesr.more.rs.geo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import repast.simphony.query.Query;
import repast.simphony.query.space.gis.ContainsQuery;
import repast.simphony.query.space.gis.GeographyWithin;
import repast.simphony.query.space.gis.WithinQuery;
import repast.simphony.space.gis.Geography;

import com.vividsolutions.jts.geom.Geometry;

import de.cesr.more.geo.MTorusCoordinate;

/**
 * MoRe
 * 
 * @author Sascha Holzhauer
 * @param <AgentType>
 *            The type of objects contained in the wrapped geography
 * @date 22.07.2010
 * 
 */
public class MGeographyWrapper<AgentType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MGeographyWrapper.class);

	private Geography<AgentType> geography = null;
	
	protected MoreWithinQueryFactory<AgentType> queryFac;
	
	
	static class GeographyWithinFactory<AgentType> implements MoreWithinQueryFactory<AgentType> {

		/**
		 * @see de.cesr.more.rs.geo.util.MoreWithinQueryFactory#initQuery(repast.simphony.space.gis.Geography, double, java.lang.Object)
		 */
		@Override
		public Query<AgentType> initQuery(Geography<AgentType> geography,
				double distance, AgentType sourceObject) {
			return new GeographyWithin<AgentType>(geography, distance, sourceObject);
		}
	}

	static class MGeoDistanceFactory<AgentType> implements MoreWithinQueryFactory<AgentType> {

		/**
		 * @see de.cesr.more.rs.geo.util.MoreWithinQueryFactory#initQuery(repast.simphony.space.gis.Geography, double, java.lang.Object)
		 */
		@Override
		public Query<AgentType> initQuery(Geography<AgentType> geography,
				double distance, AgentType sourceObject) {
			// TODO test!
			if (geography.getLayer(MTorusCoordinate.class) != null) {
				return new MGeoTorusDistanceQuery<AgentType>(geography, distance, sourceObject);
			} else {
				return new GeographyWithin<AgentType>(geography, distance, sourceObject);
			}
		}
	}
	
	/**
	 * @param geography
	 */
	public MGeographyWrapper(Geography<AgentType> geography) {
		this.geography = geography;
		this.queryFac = new MGeoDistanceFactory<AgentType>();
	}

	/**
	 * Find <numAgents> agents who are next to the focal agent within the given
	 * area. Starting with a radius of <code>radius</code> the radius is
	 * expanded by <code>radius</code> until enough agents are found or the
	 * number of total objects within the geography is reached.
	 * 
	 * Checks if the the number of all agents within the given area is
	 * larger or equal to the requested number.
	 * 
	 * @param focus
	 * @param numAgents
	 * @param area
	 * @param radius
	 *            radius to search agents within in meters
	 * @param returnClass
	 * @return Collection of agents in the area
	 */
	@SuppressWarnings("unchecked")
	public <ReturnType> List<ReturnType> getSurroundingNAgents(
			AgentType focus, int numAgents, Geometry area, double radius,
			Class<ReturnType> returnClass) {

		
		logger.info("Number of requested objects within area: " + numAgents);

		List<ReturnType> agents = new ArrayList<ReturnType>(numAgents);
		int totalNumObject = 0;

		ContainsQuery<AgentType> containsQuery = new ContainsQuery<AgentType>(
				this.geography, area);
		for (AgentType agent : containsQuery.query()) {
			if (returnClass.isInstance(agent)) {
				totalNumObject++;
			}
			if (logger.isDebugEnabled()) {
				logger.info("Within: " + geography.getGeometry(agent));
			}
		}
		totalNumObject -= 1; // - focal agent
		logger.info("Total num of objects within area: " + totalNumObject);

		Collection<ReturnType> addedAgents = new ArrayList<ReturnType>();

		while (agents.size() < numAgents && agents.size() < totalNumObject) {
			if (logger.isDebugEnabled()) {
				logger.debug("Current radius: " + radius + " / Found agents: "
						+ agents.size());
			}
			Query<AgentType> queryWithin = this.queryFac.initQuery(
					this.geography, radius, focus);
			for (AgentType agent : queryWithin.query(containsQuery.query())) {
				if (logger.isDebugEnabled()) {
					logger.debug("Query yielded " + agent + "("
							+ geography.getGeometry(agent) + ")" + " within "
							+ radius + " from focus "
							+ geography.getGeometry(focus));
					logger.debug("Return class: " + returnClass
							+ " / current class " + agent.getClass());
				}
				if (returnClass.isInstance(agent)
						&& !agents.contains(agent)) {
					if (logger.isDebugEnabled()) {
						logger.debug("add " + agent);
					}
					addedAgents.add((ReturnType) agent); // unchecked cast - agent is assignable to a Class<ReturnType>
				}
			}
			if (agents.size() + addedAgents.size() <= numAgents) {
				agents.addAll(addedAgents);
			} else {
				// add agents to a sorted map
				Map<Double, Collection<ReturnType>> agentMap = new TreeMap<Double, Collection<ReturnType>>();
				for (ReturnType agent : addedAgents) {
					if (logger.isDebugEnabled()) {
						logger.info("Distance for "
								+ agent
								+ ": "
								+ this.geography.getGeometry(focus).distance(
										this.geography.getGeometry(agent)));
					}
					Double value = new Double(this.geography.getGeometry(focus)
							.distance(this.geography.getGeometry(agent)));
					if (!agentMap.containsKey(value)) {
						agentMap.put(value, new ArrayList<ReturnType>());
					}
					agentMap.get(value).add(agent);
				}
				// fetch the nearest:
				for (Collection<ReturnType> collection : agentMap.values()) {
					for (ReturnType agent : collection) {
						agents.add(agent);
						if (logger.isDebugEnabled()) {
							logger.info("Distance for added"
									+ agent
									+ ": "
									+ this.geography.getGeometry(focus).distance(
											this.geography.getGeometry(agent)));
						}
						if (numAgents == agents.size()) {
							break;
						}
					}
				}

			}
			addedAgents.clear();
			radius += radius;
		}
		if (logger.isDebugEnabled()) {
			logger.info("Retrun agents: " + agents);
		}
		return agents;
	}

	/**
	 * Find <numAgents> agents who are next to the focal agent within the given
	 * area. Starting with a radius of <code>radius</code> the radius is
	 * expanded by <code>radius</code> until enough agents are found or the
	 * number of total objects within the geography is reached.
	 * 
	 * @param focus
	 * @param numAgents
	 * @param radius
	 *            radius to search agents within in meters
	 * @param returnClass
	 * @return Collection of agents in the area
	 */
	@SuppressWarnings("unchecked")
	public <ReturnType> List<ReturnType> getSurroundingNAgents(AgentType focus,
			int numAgents, double radius, Class<ReturnType> returnClass) {

		logger.info("Number of requested objects within area: " + numAgents);

		List<ReturnType> agents = new ArrayList<ReturnType>(numAgents);
		int totalNumObject = 0;

		for (AgentType agent : geography.getAllObjects()) {
			if (returnClass.isInstance(agent)) {
				totalNumObject++;
			}
			if (logger.isDebugEnabled()) {
				logger.info("Within: " + geography.getGeometry(agent));
			}
		}
		totalNumObject -= 1; // - focal agent
		logger.info("Total num of objects within area: " + totalNumObject);

		Collection<ReturnType> addedAgents = new ArrayList<ReturnType>();

		while (agents.size() < numAgents && agents.size() < totalNumObject) {
			if (logger.isDebugEnabled()) {
				logger.debug("Current radius: " + radius + " / Found agents: "
						+ agents.size());
			}
			Query<AgentType> queryWithin = this.queryFac.initQuery(
					this.geography, radius, focus);
			for (AgentType agent : queryWithin.query()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Query yielded " + agent + "("
							+ geography.getGeometry(agent) + ")" + " within "
							+ radius + " from focus "
							+ geography.getGeometry(focus));
					logger.debug("Return class: " + returnClass
							+ " / current class " + agent.getClass());
				}
				if (returnClass.isInstance(agent)
						&& !agents.contains(agent)) {
					if (logger.isDebugEnabled()) {
						logger.debug("add " + agent);
					}
					addedAgents.add((ReturnType) agent); // unchecked cast - agent is assignable to a Class<ReturnType>
				}
			}
			if (agents.size() + addedAgents.size() <= numAgents) {
				agents.addAll(addedAgents);
			} else {
				// add agents to a sorted map
				Map<Double, Collection<ReturnType>> agentMap = new TreeMap<Double, Collection<ReturnType>>();
				for (ReturnType agent : addedAgents) {
					if (logger.isDebugEnabled()) {
						logger.info("Distance for "
								+ agent
								+ ": "
								+ this.geography.getGeometry(focus).distance(
										this.geography.getGeometry(agent)));
					}
					Double value = new Double(this.geography.getGeometry(focus)
							.distance(this.geography.getGeometry(agent)));
					if (!agentMap.containsKey(value)) {
						agentMap.put(value, new ArrayList<ReturnType>());
					}
					agentMap.get(value).add(agent);
				}
				// fetch the nearest:
				for (Collection<ReturnType> collection : agentMap.values()) {
					for (ReturnType agent : collection) {
						agents.add(agent);
						if (logger.isDebugEnabled()) {
							logger.info("Distance for added"
									+ agent
									+ ": "
									+ this.geography.getGeometry(focus).distance(
											this.geography.getGeometry(agent)));
						}
						if (numAgents == agents.size()) {
							break;
						}
					}
				}

			}
			addedAgents.clear();
			radius += radius;
		}
		if (logger.isDebugEnabled()) {
			logger.info("Retrun agents: " + agents);
		}
		return agents;
	}

	/**
	 * Find agents who are next to the focal agent within the given maximum
	 * radius. Does not consider areas ({@link AreaContext}. Checks for return
	 * class.
	 * 
	 * @param <ReturnType>
	 * 
	 * @param focus
	 * @param radius  radius to search agents within in meters
	 * @param returnClass
	 * @return list of surrounding agents
	 */
	@SuppressWarnings("unchecked")
	public <ReturnType> List<ReturnType> getSurroundingAgents(
			AgentType focus, double radius, Class<? extends ReturnType> returnClass) {

		logger.info("Requested radius: " + radius);

		List<ReturnType> agents = new ArrayList<ReturnType>();

		if (logger.isDebugEnabled()) {
			logger.debug("Current radius: " + radius + " / Found agents: "
					+ agents.size());
		}
		
		Query<AgentType> queryWithin = this.queryFac.initQuery(
				this.geography, radius, focus);
		
		for (AgentType agent : queryWithin.query()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Query yielded " + agent + "("
						+ geography.getGeometry(agent) + ")" + " within "
						+ radius + " from focus " 
						+ geography.getGeometry(focus));
				logger.debug("Return class: " + returnClass
						+ " / current class " + agent.getClass());
			}
			if (returnClass.isInstance(agent)
					&& !agents.contains(agent)) {
				if (logger.isDebugEnabled()) {
					logger.debug("add " + agent);
				}
				agents.add((ReturnType) agent);  // unchecked cast - agent is assignable to a Class<ReturnType>
			}
		}
		if (logger.isDebugEnabled()) {
			logger.info("Return agents: " + agents);
		}
		return agents;
	}

	/**
	 * @param area
	 * @param returnClass
	 * @return sum of agents of type return type in area
	 */
	public <ReturnType> int getMaxNumAgents(Geometry area, Class<ReturnType> returnClass) {
		int totalNumObject = 0;
		ContainsQuery<AgentType> containsQuery = new ContainsQuery<AgentType>(
				this.geography, area);
		for (AgentType agent : containsQuery.query()) {
			if (returnClass.isInstance(agent)) {
				totalNumObject++;
			}
		}
		return totalNumObject;
	}

	/**
	 * @param returnClass
	 * @return sum of agents of type return type in the entire geography
	 */
	public <ReturnType> int getMaxNumAgents(Class<ReturnType> returnClass) {
		int totalNumObject = 0;
		for (AgentType agent : geography.getAllObjects()) {
			if (returnClass.isInstance(agent)) {
				totalNumObject++;
			}
		}
		return totalNumObject;
	}

	/**
	 * @param geography
	 * @param agent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <AreaType> AreaType getContainingAreaContext(
			AgentType agent, Class<AreaType> areaClass) {
		WithinQuery<AgentType> withinQuery = new WithinQuery<AgentType>(
				geography, geography.getGeometry(agent));
		Iterator<AgentType> iterator = withinQuery.query().iterator();

		Object o = iterator.next();

		while (!(areaClass.isInstance(o)) && iterator.hasNext()) {
			o = iterator.next();
		}
		if (areaClass.isInstance(o)) {
			return (AreaType) o; // unchecked cast - o is assignable to a Class<AreaType>
		} else {
			return null;
		}
	}
}
