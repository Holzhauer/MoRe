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


import repast.simphony.space.gis.Geography;
import de.cesr.more.geo.MTorusCoordinate;
import de.cesr.parma.core.PmParameterDefinition;


/**
 * Basic parameter definitions for MORe (e.g. Torus boundaries)
 * @author Sascha Holzhauer
 * @date 29.06.2010
 *
 */
public enum MBasicPa implements PmParameterDefinition {

	MILIEU_START_ID(Integer.class, new Integer(1)),

	/**
	 * Lower X corner coordinate of the torus
	 * used in {@link MTorusCoordinate} to calculate distances.
	 */
	TORUS_FIELD_LOWER_X(Double.class, new Double(0.0)),

	/**
	 * Lower Y corner coordinate of the torus
	 * used in {@link MTorusCoordinate} to calculate distances.
	 */
	TORUS_FIELD_LOWER_Y(Double.class, new Double(0.0)),

	/**
	 * Upper X corner coordinate of the torus
	 * used in {@link MTorusCoordinate} to calculate distances.
	 */
	TORUS_FIELD_UPPER_X(Double.class, new Double(100.0)),

	/**
	 * Upper Y corner coordinate of the torus
	 * used in {@link MTorusCoordinate} to calculate distances.
	 */
	TORUS_FIELD_UPPER_Y(Double.class, new Double(100.0)),

	/**
	 * Used to initialise the {@link GeometryFactory}. Should be UTM
	 */
	SPATIALREFERENCEID(Integer.class, new Integer(32632 )),

	/**
	 * Geography in the RS root context
	 */
	ROOT_GEOGRAPHY(Geography.class, null),

	/**
	 * Used to round double up values that need to sum up to a certain value (e.g. link preferences). If for some
	 * reason, un-critical errors occur because of many summands that are not exactly represented this parameter can be
	 * set to around 10000000.
	 */
	PRECISION_FACTOR(Integer.class, -1);


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