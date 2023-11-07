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
 * Created by Sascha Holzhauer on 18 Aug 2014
 */
package de.cesr.more.param;

import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 18 Aug 2014 
 *
 */
public enum MNetBuildErdosRenyiPa implements PmParameterDefinition {
	
	/**
	 * The average degree of a node in the resulting network. Determines p 
	 * ( 1/#agents * AVG_DEGREE).
	 */
	K(Integer.class, 6);
	
	private Class<?>	type;
	private Object		defaultValue;

	/**
	 * @param type
	 */
	MNetBuildErdosRenyiPa(Class<?> type) {
		this(type, null);
	}

	/**
	 * @param type
	 * @param defaultValue
	 */
	MNetBuildErdosRenyiPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	/**
	 *
	 */
	private MNetBuildErdosRenyiPa(Class<?> type, PmParameterDefinition defaultDefinition) {
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
