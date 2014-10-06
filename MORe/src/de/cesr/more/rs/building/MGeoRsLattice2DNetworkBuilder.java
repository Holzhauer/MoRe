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

import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.geo.manipulate.MoreGeoNetworkEdgeModifier;
import de.cesr.more.param.MNetBuildLattice2DPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.edge.MGeoRsNetworkEdgeModifier;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 * 
 * Uses a {@link MGeoRsNetworkEdgeModifier}.
 * 
 * <table>
 * <th>Parameter</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents; must be quadratic)</td></tr>
 * <th>Property</th><th>Value</th>
 * <tr><td>#Edges:</td><td>Toroidal & directed: 4N - 2sqrt(N); non-toroidal & directed: 4N - 4sqrt(N)</td></tr>
 * <tr><td></td><td></td></tr>
 * </table>
 * <br>
 * 
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetBuildLattice2DPa.TOROIDAL}</li>
 * </ul>
 *
 * @author holzhauer
 * @date 22.11.2011 
 *
 */
public class MGeoRsLattice2DNetworkBuilder<AgentType, EdgeType extends MRepastEdge<AgentType>> extends
MRsLattice2DNetworkBuilder<AgentType, EdgeType> implements MoreGeoRsNetworkBuilder<AgentType, EdgeType>{

	protected Context<AgentType>	context;
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked") // risky but unavoidable
	public MGeoRsLattice2DNetworkBuilder() {
		this((MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>(), "Network");
	}
	
	/**
	 * @param eFac
	 * @param name
	 */
	public MGeoRsLattice2DNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		this(eFac, name, PmParameterManager.getInstance(null));
	}

	public MGeoRsLattice2DNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name,
			PmParameterManager pm) {
		super(eFac, name, pm);
		this.edgeModifier = new MGeoRsNetworkEdgeModifier<AgentType, EdgeType>(eFac);
	}
	
	
	@Override
	public void setGeography(Geography<Object> geography) {
		((MoreGeoNetworkEdgeModifier<AgentType, EdgeType>) this.edgeModifier).setGeography(geography);
	}

	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MGeoRsLattice2DNetworkBuilder";
	}
}
