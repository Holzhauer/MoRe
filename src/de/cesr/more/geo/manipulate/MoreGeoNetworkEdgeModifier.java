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
 * Created by holzhauer on 20.11.2011
 */
package de.cesr.more.geo.manipulate;

import repast.simphony.space.gis.Geography;

import com.vividsolutions.jts.geom.GeometryFactory;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;

/**
 * MORe
 *
 * @author holzhauer
 * @date 20.11.2011 
 *
 */
public interface MoreGeoNetworkEdgeModifier<AgentType, EdgeType extends MoreEdge<? super AgentType>> extends MoreNetworkEdgeModifier<AgentType, EdgeType> {
	
	/**
	 * @return the geography
	 */
	public Geography<Object> getGeography();

	/**
	 * @param geography the geography to set
	 */
	public void setGeography(Geography<Object> geography);
	/**
	 * @return the geoFactory
	 */
	public GeometryFactory getGeoFactory();
	/**
	 * @param geoFactory the geoFactory to set
	 */
	public void setGeoFactory(GeometryFactory geoFactory);
}
