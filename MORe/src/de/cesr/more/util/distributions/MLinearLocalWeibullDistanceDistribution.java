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
 * Created by Sascha Holzhauer on 20.03.2014
 */
package de.cesr.more.util.distributions;


import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 20.03.2014 
 *
 */
public class MLinearLocalWeibullDistanceDistribution extends MWeibullDistanceDistribution implements MRealDistribution {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MLinearLocalWeibullDistanceDistribution.class);

	protected double		icp_nom		= 0.0;
	protected double		icp_root	= 0.0;
	protected double		icp_pre		= 0.0;

	protected double		p_rest		= 0.0;

	protected double		fl_null		= 0.0;
	protected double		fw_xmin		= 0.0;
	protected double		llength		= 0.0;
	protected double		fl_q		= 0.0;

	/**
	 * @param rng
	 * @throws NotStrictlyPositiveException
	 */
	public MLinearLocalWeibullDistanceDistribution(RandomGenerator rng) throws NotStrictlyPositiveException {
		super(rng);
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#density(double)
	 */
	@Override
	public double density(double x) {
		if (x < this.getSupportLowerBound() || x > this.xmax) {
			return 0.0;
		}

		// local distances
		if (x <= this.xmin) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Return density for " + x + ": "
						+ (this.fl_null - x * (this.fl_null - this.fw_xmin) / this.llength));
			}
			// LOGGING ->

			//return (1 / length) * (this.p_rest + this.pLocal - (this.p_rest / length) * 2 * x);
			return this.fl_null - x * (this.fl_null - this.fw_xmin) / this.llength;

			// Weibull distributed
		} else {
			return (getWeibull().density(x) / (getWeibull().cumulativeProbability(this.xmax) -
					getWeibull().cumulativeProbability(this.xmin))) * (1 - this.pLocal);
		}
	}

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#cumulativeProbability(double)
	 */
	@Override
	public double cumulativeProbability(double x) {
		if (x <= this.getSupportLowerBound()) {
			return 0;
		} else if (x >= xmax) {
			return 1;

			// local distances (use calculation of area for trapezoids) :
		} else if (x <= xmin) {
			return (this.fl_null + this.density(x)) * x / 2.0;

			// Weibull distributed:
		} else {
			double gXmin = getWeibull().cumulativeProbability(xmin);
			// normalize Weibull for the range between xmin and xmax:
			return this.pLocal + (1.0 - this.pLocal) * (getWeibull().cumulativeProbability(x) -
					gXmin) / (getWeibull().cumulativeProbability(xmax) - gXmin);
		}
	}

	/**
	 * @see org.apache.commons.math3.distribution.AbstractRealDistribution#inverseCumulativeProbability(double)
	 */
	@Override
	public double inverseCumulativeProbability(double random) {
		if (random < this.pLocal) {
			// return this.icp_pre
			// - Math.sqrt(this.icp_root + random * (this.pLocal + getWeibull().density(xmin) * this.xmin))
			// / this.icp_nom;
			return this.fl_q
					- Math.sqrt(this.fl_q * this.fl_q - random * 2 * this.llength / (this.fl_null - this.fw_xmin));

		} else {
			double gXmin = getWeibull().cumulativeProbability(xmin);
			double x = gXmin + ((random - this.pLocal) / (1 - this.pLocal)) *
					(getWeibull().cumulativeProbability(xmax) - gXmin);
			return this.getWeibull().inverseCumulativeProbability(x);
		}
	}

	@Override
	public void init() {
		super.init();

		if (this.pLocal <= 0.0 || this.xmin <= 0.0) {
			throw new IllegalStateException("p_local and x_min must be > 0!");
		}

		// this.icp_nom = this.pLocal / this.xmin - w_xmin;
		//
		// if (this.icp_nom == 0.0) {
		// throw new IllegalStateException("Invalid parameter value combination for p_local and x_min!");
		// }
		// this.icp_root = Math.sqrt(this.pLocal) - this.pLocal * w_xmin + this.xmin + w_xmin * Math.pow(w_xmin, 2.0)
		// / 4.0;
		// this.icp_pre = this.pLocal + w_xmin * this.xmin / 2.0;
		//
		// this.p_rest = this.pLocal - w_xmin * this.xmin;

		this.fw_xmin = getWeibull().density(xmin);
		this.llength = this.xmin - this.getSupportLowerBound();
		this.fl_null = 2 * this.pLocal / this.llength - this.fw_xmin;
		this.fl_q = this.llength * this.fl_null / (this.fl_null - this.fw_xmin);
	}
}
