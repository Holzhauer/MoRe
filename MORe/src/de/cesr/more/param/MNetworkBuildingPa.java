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


import java.util.Map;

import com.vividsolutions.jts.geom.GeometryFactory;

import de.cesr.more.geo.building.network.MGeoRestoreNetworkService;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.building.MGeoRsNetworkService;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * Parameter definitions used for MORe's network building
 *
 * @author Sascha Holzhauer
 * @date 23.09.2011 
 *
 */
public enum MNetworkBuildingPa implements PmParameterDefinition {
	
	/**
	 * The milieu network parameter map is usually read by {@link MMilieuNetDataReader}.
	 * Default is <code>null</code>.
	 */
	MILIEU_NETWORK_PARAMS(MMilieuNetworkParameterMap.class, null),
	
	/**
	 * Probability to connect to a specific milieu. Actually a map with double for each milieu. Default:
	 * <code>1.0/MNetworkBuildingPa.MILIEUS</code> (if not null - 0.5 otherwise). Values are normally taken from
	 * {@link MMilieuNetworkParameterMap} but this parameter definition is required to access values in the map.
	 */
	@SuppressWarnings("unchecked")
	// parameter definition
	P_MILIEUS(Double.class, new Double(1.0 /
			(PmParameterManager.
					getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS) != null ?
					((Map<Integer, Map<PmParameterDefinition, Object>>) PmParameterManager.
							getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS)).size() : 2))),

	/**
	 * Used by e.g. {@link MGeoRestoreNetworkService}.
	 */
	MAINTAINING_NETWORK_SERVICE(Class.class, null),

	MILIEU_NETWORK_CSV_MILIEUS(String.class, ""),

	MILIEU_NETWORK_CSV_MILIEULINKS(String.class, ""),
	
	MILIEU_NETWORK_CSV_DELIMITER(Character.class, ','),

	MILIEU_NETWORK_CSV_COLUMNPREFIX(String.class, "de.cesr.more.param."),

	/**
	 * The parameter id used to retrieve network preference data
	 * from tables {@link MSqlPa#TBLNAME_NET_PREFS} and {@link MSqlPa#TBLNAME_NET_PREFS_LINKS}.
	 * Default: <code>0</code>
	 */
	MILIEU_NETPREFS_PARAMID(Integer.class, 0),

	/*****************************************************
	 * Random network Builder
	 *****************************************************/
	
	/**
	 * The average degree of a node in the resulting network. Determines p ( 1/#agents * AVG_DEGREE).
	 */
	BUILD_RANDOM_AVG_DEGREE(Integer.class, 6),
	
	/*****************************************************
	 * Watts-Strogats Small-World network Builder
	 *****************************************************/

	/**
	 * The probability of an edge being rewired randomly; the proportion of randomly rewired edges in a graph. Range:
	 * <code>(0,1)</code>; Default: <code>0.1</code>.
	 * 
	 * @deprecated use {@link MNetBuildWsPa#BETA}
	 */
	BUILD_WSSM_BETA(Double.class, 0.1),

	/**
	 * Initial degree that is used to build to regular network (local neighbourhood size) to start from. Must be an even
	 * number. Default: <code>4</code>. If BUILD_WSSM_CONSIDER_SOURCES is TRUE, this value is considered as in-degree!
	 * 
	 * @deprecated use {@link MNetBuildWsPa#K}
	 */
	BUILD_WSSM_INITIAL_OUTDEG(Integer.class, 4),
	
	
	/**
	 * Regarding the SmallWorldNetworkBuilder one must pay attention because of the network direction.
	 * Generally, the small world algorithm considers given k and beta values for the source of a direction.
	 * However, in some models we consider the influencer as source and seek to build the network according
	 * to the influenced' properties. In this case, set this parameter to FALSE.
	 */
	BUILD_WSSM_CONSIDER_SOURCES(Boolean.class, Boolean.TRUE),
	
	/*****************************************************
	 * Restore network Builder
	 *****************************************************/
	
	RESTORE_NETWORK_SOURCE_FILE(String.class, "network"),
	
	/*****************************************************
	 * General network Settings
	 *****************************************************/
	
	/**
	 * "Directedness" of the generated networks.
	 * Default:<code>true</code>.
	 */
	BUILD_DIRECTED(Boolean.class, true),
	
	
	
	/*****************************************************
	 * General Geography Settings
	 *****************************************************/

	/**
	 * If true, edges are added to the geography (adding many edges to the geography is a performance issue). Default:
	 * <code>false</code>.
	 */
	ADD_EDGES_TO_GEOGRAPHY(Boolean.class, false),
	
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
	 * 
	 */
	private MNetworkBuildingPa(Class<?> type, PmParameterDefinition defaultDefinition) {
		this.type = type;
		if (defaultDefinition != null) {
			this.defaultValue = defaultDefinition.getDefaultValue();
			PmParameterManager.setDefaultParameterDef(this, defaultDefinition);
		} else {
			this.defaultValue = null;
		}
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