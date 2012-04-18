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
 * Created by Sascha Holzhauer on 28.03.2012
 */
package de.cesr.more.param;

import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 28.03.2012 
 *
 */
public enum MNetManipulatePa implements PmParameterDefinition {

	INCREASE_THRESHOLD(Double.class, 0.1),
	DECREASE_THRESHOLD(Double.class, 0.5),

	INCREASE_AMOUNT(Double.class, 0.1),

	DYN_DECREASE_AMOUNT(Double.class, 0.1),

	DYN_FADE_OUT_AMOUNT(Double.class, 0.000),
	DYN_FADE_OUT_INTERVAL(Double.class, 1.0),
	
	DYN_PROP_RECIPROCITY(Double.class, 1.0),
	DYN_PROP_TRANSITIVIY(Double.class, 1.0),
	DYN_PROP_GLOBAL(Double.class, 1.0),
	
	/**
	 * Determines the level of perfectionism with which agents
	 * choose optimal partners. Takes only effect in case
	 * the instance of {@link MoreEgoNetworkProcessor} considers the parameter.
	 * The higher the parameter the better the perfectionism. 
	 * 
	 */
	DYN_EDGE_MANAGE_OPTIMUM(Double.class, 1.0),

	DYN_INTERVAL_EDGE_UPDATING(Integer.class, 0),
	DYN_INTERVAL_LINK_MANAGEMENT(Integer.class, 0),
	
	/**
	 * Connect agents only to those the focal agents has
	 * not been connected before.
	 */
	DYN_USE_BLACKLIST(Boolean.class, Boolean.FALSE);

	private Class<?>	type;
	private Object		defaultValue;

	/**
	 * @param type
	 */
	MNetManipulatePa(Class<?> type) {
		this(type, null);
	}

	/**
	 * @param type
	 * @param defaultValue
	 */
	MNetManipulatePa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	/**
	 * 
	 */
	private MNetManipulatePa(Class<?> type, PmParameterDefinition defaultDefinition) {
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
