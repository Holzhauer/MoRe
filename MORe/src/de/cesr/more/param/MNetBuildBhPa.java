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
 * Created by holzhauer on 31.10.2011
 */
package de.cesr.more.param;


import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * Parameter definitions for the Baseline hHomophily network builder
 *
 * @author holzhauer
 * @date 31.10.2011 
 *
 */
public enum MNetBuildBhPa implements PmParameterDefinition {
	
	/**
	 * (In-) Degree. Default: <code>4</code>
	 */
	K(Integer.class, new Integer(4)),
	
	/**
	 * Probability to rewire a link. Default: <code>0.1</code>
	 */
	P_REWIRE(Double.class, new Double(0.1)),
	
	/**
	 * Probability to connect to a specific milieu. Actually a map with double for each milieu.
	 * Default: <code>1.0/MNetworkBuildingPa.NUM_MILIEU_GROUPS</code>
	 */
	P_MILIEUS(Double.class, new Double(1.0 / ((Integer)PmParameterManager.getParameter(MNetworkBuildingPa.NUM_MILIEU_GROUPS)).intValue())),
	
	/**
	 * Initial radius to search for local partner agents within in meters.
	 * Default:<code>1000</code>
	 */
	SEARCH_RADIUS(Double.class, new Double(1000.0)),
	
	/**
	 * Extension of search radius in meters.
	 * Default:<code>500</code>
	 */
	X_SEARCH_RADIUS(Double.class, new Double(500.0)),
	
	/**
	 * Max. radius to search for local partner agents within in meters.
	 * Default:<code>10000</code>
	 */
	MAX_SEARCH_RADIUS(Double.class, new Double(10000.0)),
	
	/**
	 * Weight for geographical proximity regarding partner homophily.
	 * Default: <code>0.5</code>
	 */
	DIM_WEIGHTS_GEO(Double.class, new Double(0.5)),
	
	/**
	 * Weight for milieu regarding partner homophily.
	 * Default: <code>0.5</code>
	 */
	DIM_WEIGHTS_MILIEU(Double.class, new Double(0.5));

	private Class<?> type;
	private Object defaultValue;
	
	/**
	 * @param type
	 */
	MNetBuildBhPa(Class<?> type) {
		this(type, null);
	}

	/**
	 * @param type
	 * @param defaultValue
	 */
	MNetBuildBhPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	/**
	 * @see de.cesr.parma.core.PmParameterDefinition#getType()
	 */
	@Override
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * @see de.cesr.parma.core.PmParameterDefinition#getDefaultValue()
	 */
	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
}
