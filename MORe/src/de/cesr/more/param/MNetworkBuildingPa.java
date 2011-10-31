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

import de.cesr.more.util.MDefaultMilieuKeysMap;
import de.cesr.parma.core.PmParameterDefinition;

/**
 * Parameter definitions used for MORe's network building
 *
 * @author Sascha Holzhauer
 * @date 23.09.2011 
 *
 */
public enum MNetworkBuildingPa implements PmParameterDefinition {
	
	/**
	 * The milieu parameter map is usually read by {@link sql.MilieuNetDataReader}
	 */
	MILIEU_NETWORK_PARAMS(MMilieuNetworkParameterMap.class, new MMilieuNetworkParameterMap()),
	
	
	/**
	 * The parameter id used to retrieve network preference data
	 * from tables {@link MSqlPa#TBLNAME_NET_PREFS} and {@link MSqlPa#TBLNAME_NET_PREFS_LINKS}.
	 * Default: <code>0</code>
	 */
	MILIEU_NETPREFS_PARAMID(Integer.class, 0),
	
	/**
	 * Defines the indices for milieus. Given the milieu's short name (i.e. "GLM"),
	 * the index may be queried from the map.
	 */
	MILIEUS(MDefaultMilieuKeysMap.class, new MDefaultMilieuKeysMap()),

	/**
	 * Number of milieu groups as defined in {@link SqlPa#TBLNAME_MILIEU_GROUPS}.
	 * Default: <code>4</code>
	 */
	NUM_MILIEU_GROUPS(Integer.class, new Integer(4)),
	
	
	/*****************************************************
	 * Watts-Strogats Small-World network Builder
	 *****************************************************/
	
	/**
	 * The probability of an edge being rewired randomly; the proportion of randomly
	 * rewired edges in a graph. Range: <code>(0,1)</code>; Default: <code>0.1</code>.
	 */
	BUILD_WSSM_BETA(Double.class, 0.1),
	
	/**
	 * Initial degree that is used to build to regular network (local neighbourhood size)
	 * to start from. Must be an even number. Default: <code>4</code>.
	 */
	BUILD_WSSM_INITIAL_OUTDEG(Integer.class, 4),
	
	/**
	 * "Directedness" of the generated networks. Default:<code>false</code>.
	 */
	BUILD_DIRECTED(Boolean.class, false),
	
	
	/**
	 * If true, edges are added to the geography (adding many edges to the geography is a
	 * performance issue). Default: <code>true</code>.
	 */
	ADD_EDGES_TO_GEOGRAPHY(Boolean.class, true),
	
	/**
	 * Used to initialize the {@link GeometryFactory} in {@link MGeoRsNetworkService}.
	 * Default: <code>4326</code> (WGS 84)
	 */
	SPATIAL_REFERENCE_ID(Integer.class, new Integer(4326))
	;

	
	private Class<?>	type;
	private Object		defaultValue;

	/**
	 * @param type
	 */
	MNetworkBuildingPa(Class<?> type) {
		this(type, null);
	}

	/**
	 * @param type
	 * @param defaultValue
	 */
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