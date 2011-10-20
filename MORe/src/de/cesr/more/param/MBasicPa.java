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
 * Created by Sascha Holzhauer on 29.06.2010
 */
package de.cesr.more.param;


import de.cesr.parma.core.PmParameterDefinition;


/**
 * MORe
 * @author Sascha Holzhauer
 * @date 29.06.2010 
 *
 */
public enum MBasicPa implements PmParameterDefinition {
	
	
	/**
	 * ID of used parameter set
	 */
	PARAMS_ID(Integer.class, 1),
	
	/**
	 * Used to initialise the root geography
	 */
	//CRS(String.class, DefaultGeographicCRS.WGS84),
	
	FIELD_LOWER_X(Double.class, new Double(0.0)),
	FIELD_LOWER_Y(Double.class, new Double(0.0)),

	FIELD_UPPER_X(Double.class, new Double(100.0)),
	FIELD_UPPER_Y(Double.class, new Double(100.0));
	
	private Class<?> type;
	private Object defaultValue;
	
	MBasicPa(Class<?> type) {
		this(type, null);
	}

	MBasicPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	@Override
	public Class<?> getType() {
		return type;
	}
	
	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
}
