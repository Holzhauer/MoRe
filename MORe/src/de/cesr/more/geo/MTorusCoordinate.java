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
package de.cesr.more.geo;


import com.vividsolutions.jts.geom.Coordinate;

import de.cesr.more.param.MBasicPa;
import de.cesr.parma.core.PmParameterManager;



/**
 * Enable correct distance calculations in a torus
 *
 * @author holzhauer
 * @date 31.10.2011 
 *
 */
public class MTorusCoordinate extends Coordinate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3428403064749738657L;

	public MTorusCoordinate(double x, double y) {
		super(x, y);
	}

	/**
	 * Calculates distances in a torus
	 * @see com.vividsolutions.jts.geom.Coordinate#distance(com.vividsolutions.jts.geom.Coordinate)
	 */
	public double distance(Coordinate p) {
		
		double dx = Math.abs(p.x - x);
		double dy = Math.abs(p.y - y);

		double minX = Math.min(dx, ((Double)PmParameterManager.getParameter(MBasicPa.TORUS_FIELD_UPPER_X)).doubleValue() - 
				((Double)PmParameterManager.getParameter(MBasicPa.TORUS_FIELD_LOWER_X)).doubleValue() - dx);
		minX *= minX;

		double minY = Math.min(dy, (((Double)PmParameterManager.getParameter(MBasicPa.TORUS_FIELD_UPPER_Y)).doubleValue() - 
				((Double)PmParameterManager.getParameter(MBasicPa.TORUS_FIELD_LOWER_Y)).doubleValue() - dy));
		minY *= minY;

		return Math.sqrt(minX + minY);
	}
}
