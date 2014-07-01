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

import java.util.Collection;
import java.util.Map;

import repast.simphony.space.gis.Geography;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author sholzhau
 * @date 13 Jun 2014
 *
 */
public interface MoreGeoHexagonInitialiser<AgentType> {

	/**
	 * Initialises hexagons using a shape-file loader which also adds them to the root context.
	 *
	 * @param pm
	 * @param geography
	 */
	public void init(PmParameterManager pm, Geography<Object> geography);

	/**
	 * Assigns agents to {@link MoreGeoHexagon} and hexagons to the agentHexagons map.
	 * Sets distances at hexagons.
	 *
	 * @param agents
	 * @param agentHexagons
	 */
	public void initDistanceMatrix(Collection<AgentType> agents, Map<AgentType,
			MoreGeoHexagon<AgentType>> agentHexagons, Geography<Object> geography);
	
	/**
	 * Required to identify the correct layer in geography
	 * 
	 * @return
	 */
	public Class<? extends MoreGeoHexagon> getHexagonType();
}
