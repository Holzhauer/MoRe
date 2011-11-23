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
 * Created by holzhauer on 22.11.2011
 */
package de.cesr.more.rs.building;

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.geo.building.MoreGeoNetworkEdgeModifier;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MLattice2DNetworkBuilder;
import de.cesr.more.building.util.MLattice2DGenerator;
import de.cesr.more.param.MNetBuildLattice2DPa;
import de.cesr.more.rs.building.edge.MGeoRsNetworkEdgeModifier;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author holzhauer
 * @date 22.11.2011 
 *
 */
public class MGeoRsLattice2DNetworkBuilder<AgentType, EdgeType extends MRepastEdge<AgentType> & MoreEdge<AgentType>> extends
	MLattice2DNetworkBuilder<AgentType, EdgeType> {

	/**
	 * 
	 */
	public MGeoRsLattice2DNetworkBuilder() {
		this((MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>(), "Network");
	}

	public MGeoRsLattice2DNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(eFac, name);
		this.edgeModifier = new MGeoRsNetworkEdgeModifier<AgentType, EdgeType>(eFac);
		this.latticeGenerator = new MLattice2DGenerator<AgentType, EdgeType>(
				(Boolean)PmParameterManager.getParameter(MNetBuildLattice2DPa.TOROIDAL));
	}
	
	public void setGeography(Geography<Object> geography) {
		((MoreGeoNetworkEdgeModifier<AgentType, EdgeType>) this.edgeModifier).setGeography(geography);
	}
}
