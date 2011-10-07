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
 */
package de.cesr.more.param;

import de.cesr.more.util.MDefaultMilieuKeysMap;
import de.cesr.parma.core.PmParameterDefinition;

/**
 * MORe
 * @author holzhauer
 *
 */
public enum MMilieuPa implements PmParameterDefinition {



	/**
	 * Parameter ID for milieu's goal preferenceWeights
	 */
	MILIEU_PREF_PARAMID(Integer.class, 1),
	
	/**
	 * Defines the indices for milieus. Given the milieu's short name (i.e. "GLM"), the index may be queried from the map:
	 */
	MILIEUS(MDefaultMilieuKeysMap.class, new MDefaultMilieuKeysMap()),

	/**
	 * Number of milieu groups as defined in {@link SqlPa#TBLNAME_MILIEU_GROUPS}.
	 */
	NUM_MILIEU_GROUPS(Integer.class, new Integer(4)), NUM_HHSIZES(Integer.class, new Integer(5)), NUM_YEARS(
			Integer.class, new Integer(4));



	private Class < ? >	type;
	private Object		defaultValue;

	MMilieuPa(Class < ? > type) {
		this(type, null);
	}

	MMilieuPa(Class < ? > type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	@Override
	public Class < ? > getType() {
		return type;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
}
