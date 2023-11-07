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
 * Created by holzhauer on 28.09.2011
 */
package de.cesr.more.rs.building.edge;

import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.RepastEdge;

import com.vividsolutions.jts.geom.GeometryFactory;

import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.geo.MoreGeoEdge;
import de.cesr.more.geo.building.edge.MGeoNetworkEdgeModifier;

/**
 * MORe
 *
 * @author holzhauer
 * @date 28.09.2011 
 *
 */
public class MGeoRsNetworkEdgeModifier<AgentType, EdgeType extends RepastEdge<? super AgentType> & MoreGeoEdge<? super AgentType>>
		extends
		MGeoNetworkEdgeModifier<AgentType, EdgeType> {
	
	public MGeoRsNetworkEdgeModifier() {
		super();
	}
	
	public MGeoRsNetworkEdgeModifier(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		super(edgeFac);
	}
	
	public MGeoRsNetworkEdgeModifier(MoreEdgeFactory<AgentType, EdgeType> edgeFac, Geography<Object> geography,
			GeometryFactory geoFactory) {
		super(edgeFac, geography, geoFactory);
	}
}
