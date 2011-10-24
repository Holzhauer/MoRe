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
package de.cesr.more.param;


import com.vividsolutions.jts.geom.GeometryFactory;

import de.cesr.parma.core.PmParameterDefinition;

/**
 * MORe
 *
 * @author holzhauer
 * @date 23.09.2011 
 *
 */
public enum MNetworkBuildingPa implements PmParameterDefinition {
	
	/**
	 * The milieu parameter map is usually read by {@link sql.MilieuNetDataReader}
	 */
	MILIEU_NETWORK_PARAMS(MMilieuNetworkParameterMap.class, new MMilieuNetworkParameterMap()),
	
	
	MILIEU_NETPREFS_PARAMID(Integer.class, 0),
	
	/**
	 * The probability of an edge being rewired randomly; the proportion of randomly
	 * rewired edges in a graph. Rrange: (0,1)
	 */
	BUILD_WSSM_BETA(Double.class, 0.1),
	
	/**
	 * Initial degree that is used to build to regular network (local ngh size)
	 * to start from. Must be an even number.
	 */
	BUILD_WSSM_INITIAL_OUTDEG(Integer.class, 4),
	
	BUILD_DIRECTED(Boolean.class, false),
	
	
	ADD_EDGES_TO_GEOGRAPHY(Boolean.class, true),
	
	OUTPUT_NETWORK(Boolean.class, false),
	
	NETWORK_TARGET_FILE(String.class, "./MNetwork.graphml"),
	
	/**
	 * TODO check ref id!
	 * Used to initialize the {@link GeometryFactory}.
	 */
	SPATIAL_REFERENCE_ID(Integer.class, new Integer(4326))
	;

	
	private Class<?>	type;
	private Object		defaultValue;

	MNetworkBuildingPa(Class<?> type) {
		this(type, null);
	}

	MNetworkBuildingPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * @see de.cesr.parma.core.PmParameterDefinition#getType()
	 */
	@Override
	public Class<?> getType() {
		return this.type;
	}

	/**
	 * @see de.cesr.parma.core.PmParameterDefinition#getDefaultValue()
	 */
	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

}
