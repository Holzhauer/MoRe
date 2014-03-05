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
 * Created by Sascha Holzhauer on 26.11.2011
 */
package de.cesr.more.rs.building;

import de.cesr.more.geo.building.network.MoreGeoNetworkService;
import de.cesr.more.rs.edge.MRepastEdge;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 26.11.2011 
 *
 */
public interface MoreGeoRsNetworkService <AgentType, EdgeType extends MRepastEdge<AgentType>> extends
	MoreRsNetworkService<AgentType, EdgeType>,
	MoreGeoNetworkService<AgentType, EdgeType>,
	MoreGeoRsNetworkBuilder<AgentType, EdgeType>{

	/**
	 * Set the class of which the geography wrapper requests agents. Default is the agent's superclass (in case geo
	 * request class is null).
	 * 
	 * @param geoRequestClass
	 *        the geoRequestClass to set
	 */
	public void setGeoRequestClass(Class<? extends AgentType> geoRequestClass);
}
