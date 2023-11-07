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
 * Created by Sascha Holzhauer on 25.11.2011
 */
package de.cesr.more.param;

import de.cesr.more.rs.building.MGeoRsSocialDistanceAttachNetworkBuilder;
import de.cesr.parma.core.PmParameterDefinition;

/**
 * MORe
 * 
 * Parameters for {@link MGeoRsSocialDistanceAttachNetworkBuilder}.
 *
 * @author Sascha Holzhauer
 * @date 25.11.2011 
 *
 */
public enum MNetBuildSocialAttachment implements PmParameterDefinition {
	
	DIM_WEIGHT_DEVIATION_TRESHOLD(Double.class, 0.01),

	MEAN_DISTANCE(Double.class, null);
	
	private Class<?> type;
	private Object defaultValue;
	
	/**
	 * @param type
	 */
	MNetBuildSocialAttachment(Class<?> type) {
		this(type, null);
	}

	/**
	 * @param type
	 * @param defaultValue
	 */
	MNetBuildSocialAttachment(Class<?> type, Object defaultValue) {
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
