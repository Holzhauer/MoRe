package de.cesr.more.util;


import com.vividsolutions.jts.geom.Coordinate;

import de.cesr.more.param.MBasicPa;
import de.cesr.parma.core.PmParameterManager;

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

		double minX = Math.min(dx, ((Double)PmParameterManager.getParameter(MBasicPa.FIELD_UPPER_X)).doubleValue() - 
				((Double)PmParameterManager.getParameter(MBasicPa.FIELD_LOWER_X)).doubleValue() - dx);
		minX *= minX;

		double minY = Math.min(dy, (((Double)PmParameterManager.getParameter(MBasicPa.FIELD_UPPER_Y)).doubleValue() - 
				((Double)PmParameterManager.getParameter(MBasicPa.FIELD_LOWER_Y)).doubleValue() - dy));
		minY *= minY;

		return Math.sqrt(minX + minY);
	}
}
