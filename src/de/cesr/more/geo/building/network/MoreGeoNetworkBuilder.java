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

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.building.network.MoreNetworkBuilder;

/**
 * MORe
 * Network builder implementing this interface support embedding networks in {@link Geography} projections.
 * 
 * @author holzhauer
 * @date 23.09.2011 
 *
 */
public interface MoreGeoNetworkBuilder<AgentType, EdgeType extends MoreEdge<? super AgentType>> extends
		MoreNetworkBuilder<AgentType, EdgeType> {
	
	/**
	 * Sets the geography that defines the spatial proximity of nodes.
	 * @param geography
	 */
	public void setGeography(Geography<Object> geography);

	public Geography<Object> getGeography();

}
