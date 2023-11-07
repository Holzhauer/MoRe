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
 * Created by Sascha Holzhauer on 29.11.2011
 */
package de.cesr.more.rs.building;

import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.edge.MGeoRsNetworkEdgeModifier;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 29.11.2011 
 *
 */
public abstract class MAbstractGeoRsNetworkBuilder<AgentType, EdgeType extends MRepastEdge<AgentType>> 
		implements MoreGeoRsNetworkBuilder<AgentType, EdgeType> {
	
	/**
	 * Need to be of type {@link Object} since network objects and agents should be insertable
	 */
	protected Geography<Object>		geography;
	
	protected GeometryFactory		geoFactory		= null;
	
	protected MoreEdgeFactory<AgentType, EdgeType> edgeFac = null;
	
	/**
	 * should be accessed via getEdgeModifer...
	 */
	protected MoreNetworkEdgeModifier<AgentType, EdgeType> edgeModifier;

	/**
	 * The context the network belongs to.
	 */
	protected Context<AgentType>						   context;
	
	/**
	 * @param geography
	 */
	public MAbstractGeoRsNetworkBuilder(Geography<Object> geography) {
		this();
		this.setGeography(geography);
	}
	
	/**
	 * 
	 */
	public MAbstractGeoRsNetworkBuilder() {
		this.geoFactory = new GeometryFactory(new PrecisionModel(),
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.SPATIAL_REFERENCE_ID)).intValue());
	}

	
	/*************************************
	 *   GETTER & SETTER
	 *************************************/
	
	
	/**
	 * @see de.cesr.more.geo.building.network.MoreGeoNetworkBuilder#setGeography(repast.simphony.space.gis.Geography)
	 */
	@Override
	public void setGeography(Geography<Object> geography) {
		this.geography = geography;
		this.edgeModifier = new MGeoRsNetworkEdgeModifier<AgentType, EdgeType>(this.edgeFac, geography, geoFactory);
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#setContext(repast.simphony.context.Context)
	 */
	@Override
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}

	/**
	 * @return
	 */
	public Geography<Object> getGeography() {
		return geography;
	}

	/**
	 * @return
	 */
	public Context<AgentType> getContext() {
		return context;
	}
}
