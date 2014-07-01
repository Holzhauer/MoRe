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
 * Created by sholzhau on 12 Jun 2014
 */
package de.cesr.more.rs.geo.util;

import java.util.ArrayList;
import java.util.Set;

import repast.simphony.space.gis.Geography;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author sholzhau
 * @date 12 Jun 2014
 *
 */
public interface MoreGeoHexagon<AgentType> extends Comparable<MoreGeoHexagon<AgentType>>{

	/**
	 * Get this hexagon's ID
	 * @return hexagon's ID
	 */
	public int getId();

	/**
	 * Add an agent to the hexagon
	 * @param agent
	 *    	Agent within the hexagon
	 */
	public void addAgent(AgentType agent);

	/**
	 * Remove an agent from the hexagon
	 * @param agent
	 */
	public void removeAgent(AgentType agent);

	/**
	 * @return list of agents within this hexagon
	 */
	public ArrayList<AgentType> getAgents();

	/**
	 * Sets the distance between this hexagon and the given one.
	 * @param hexagon
	 * @param distance
	 */
	public void setDistance(MoreGeoHexagon<AgentType> hexagon, Double distance);

	/**
	 * Returns a Set of hexagons that may contain agents whose distance to agents
	 * within this hexagon is the given distance.
	 * @param agent the focal agent
	 * @param distance
	 * @return
	 */
	public Set<MoreGeoHexagon<AgentType>> getHexagonsOfDistance(AgentType agent, double distance);
}
