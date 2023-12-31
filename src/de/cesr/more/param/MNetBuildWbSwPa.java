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


import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * Parameter definitions used for MORe's network building
 *
 * @author Sascha Holzhauer
 * @date 23.09.2011 
 *
 */
public enum MNetBuildWbSwPa implements PmParameterDefinition {
	
	/*****************************************************
	 * Watts-Beta Small-World network Builder
	 *****************************************************/
	
	/**
	 * The probability of an edge being rewired randomly; the proportion of randomly
	 * rewired edges in a graph. Range: <code>(0,1)</code>; Default: <code>0.1</code>.
	 */
	BETA(Double.class, 0.1),

	/**
	 * Initial degree that is used to build to regular network (local neighbourhood size) to start from. Must be an even
	 * number. Default: <code>4</code>. If BUILD_WSSM_CONSIDER_SOURCES is TRUE, this value is considered as in-degree!
	 */
	K(Integer.class, 4);
	
	
	
	private Class<?>	type;
	private Object		defaultValue;

	/**
	 * @param type
	 */
	MNetBuildWbSwPa(Class<?> type) {
		this(type, null);
	}

	/**
	 * @param type
	 * @param defaultValue
	 */
	MNetBuildWbSwPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * 
	 */
	private MNetBuildWbSwPa(Class<?> type, PmParameterDefinition defaultDefinition) {
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