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
 * Created by Sascha Holzhauer on 13.09.2013
 */
package de.cesr.more.util.distributions;


import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import de.cesr.more.rs.building.MGeoRsHomophilyDistanceFfNetworkService;


/**
 * MORe
 * 
 * Wrapper for {@link WeibullDistribution}. Used in {@link MGeoRsHomophilyDistanceFfNetworkService}.
 * 
 * @author Sascha Holzhauer
 * @date 13.09.2013
 * 
 */
public class MWeibullDistanceDistribution extends AbstractRealDistribution implements MRealDistribution {

	public enum MWeibullDistanceDistParams implements MDistributionParameter {
		SHAPE,
		SCALE,
		XMIN,
		XMAX,
		PLOCAL;
	}

	/**
	 * Logger
	 */
	static private Logger		logger				= Logger.getLogger(MWeibullDistanceDistribution.class);

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/** The shape parameter. */
	protected double			shape;
	/** The scale parameter. */
	protected double			scale;

	protected double			xmin				= 0;

	protected double			xmax				= Double.POSITIVE_INFINITY;

	protected double			pLocal;

	protected RealDistribution	weibull;
	/**
	 * Creates a Weibull distribution.
	 * 
	 * @param rng
	 *        Random number generator.
	 */
	public MWeibullDistanceDistribution(RandomGenerator rng)
			throws NotStrictlyPositiveException {
		super(rng);
	}

	/**
	 * @see de.cesr.more.util.distributions.MRealDistribution#init()
	 */
	@Override
	public void init() {
		this.weibull = new WeibullDistribution(this.random, this.shape, this.scale,
				WeibullDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}

	protected RealDistribution getWeibull() {
		if (this.weibull == null) {
			// <- LOGGING
			logger.error("init() has to be called before!");
			// LOGGING ->
			throw new IllegalStateException("init() has to be called before!");
		}
		return this.weibull;
	}

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#density(double)
	 */
	@Override
	public double density(double x) {
		if (x < this.getSupportLowerBound() || x > this.xmax) {
			return 0.0;
		}

		// local distances
		if (x <= xmin) {
			return this.pLocal / (this.xmin - this.getSupportLowerBound());

			// Weibull distributed
		} else {
			return (getWeibull().density(x) / (getWeibull().cumulativeProbability(xmax) -
					getWeibull().cumulativeProbability(xmin))) * (1 - this.pLocal);
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

			// local distances:
		} else if (x <= xmin) {
			return (x - this.getSupportLowerBound()) * this.pLocal / (this.xmin - this.getSupportLowerBound());

			// Weibull distributed:
		} else {
			double gXmin = getWeibull().cumulativeProbability(xmin);
			// normalize Weibull for the range between xmin and xmax:
			return this.pLocal + (1.0 - this.pLocal) * (getWeibull().cumulativeProbability(x) - gXmin) /
					(getWeibull().cumulativeProbability(xmax) - gXmin);
		}
	}

	/**
	 * @see org.apache.commons.math3.distribution.AbstractRealDistribution#inverseCumulativeProbability(double)
	 */
	@Override
	public double inverseCumulativeProbability(double random) {
		if (random < this.pLocal) {
			return random / this.pLocal * (this.xmin - this.getSupportLowerBound()) + this.getSupportLowerBound();
		} else {
			double gXmin = getWeibull().cumulativeProbability(xmin);
			double x = gXmin + ((random - this.pLocal) / (1 - this.pLocal)) * 
					(getWeibull().cumulativeProbability(xmax) - gXmin);
			return this.getWeibull().inverseCumulativeProbability(x);
		}
	}

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#getNumericalMean()
	 */
	@Override
	public double getNumericalMean() {
		throw new UnsupportedOperationException(
				"The special version of WeibullDistribution does not support this method!");
	}

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#getNumericalVariance()
	 */
	@Override
	public double getNumericalVariance() {
		throw new UnsupportedOperationException(
				"The special version of WeibullDistribution does not support this method!");
	}

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#getSupportLowerBound()
	 */
	@Override
	public double getSupportLowerBound() {
		return 0;
	}

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#getSupportUpperBound()
	 */
	@Override
	public double getSupportUpperBound() {
		return this.xmax;
	}

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#isSupportLowerBoundInclusive()
	 */
	@Override
	public boolean isSupportLowerBoundInclusive() {
		return true;
	}

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#isSupportUpperBoundInclusive()
	 */
	@Override
	public boolean isSupportUpperBoundInclusive() {
		return true;
	}

	/**
	 * @see org.apache.commons.math3.distribution.RealDistribution#isSupportConnected()
	 */
	@Override
	public boolean isSupportConnected() {
		return true;
	}

	/**
	 * @throws NotStrictlyPositiveException
	 *         if {@code shape <= 0} or {@code scale <= 0}.
	 * 
	 * @see de.cesr.more.util.distributions.MRealDistribution#setParameter(int, double)
	 */
	@Override
	public void setParameter(MDistributionParameter param, double value) {
		if (param.ordinal() == MWeibullDistanceDistParams.SHAPE.ordinal()) {
				if (value <= 0) {
					throw new NotStrictlyPositiveException(LocalizedFormats.SHAPE,
							value);
				}
				this.shape = value;
		} else if (param.ordinal() == MWeibullDistanceDistParams.SCALE.ordinal()) {
				if (value <= 0) {
					throw new NotStrictlyPositiveException(LocalizedFormats.SCALE,
							value);
				}
				this.scale = value;
		} else if (param.ordinal() == MWeibullDistanceDistParams.XMIN.ordinal()) {
				this.xmin = value;
		} else if (param.ordinal() == MWeibullDistanceDistParams.XMAX.ordinal()) {
				this.xmax = value;
		} else if (param.ordinal() == MWeibullDistanceDistParams.PLOCAL.ordinal()) {
				this.pLocal = value;
		} else {
			// <- LOGGING
			logger.warn("Given parameter (" + param + ") not defined for " + this);
			// LOGGING ->
		}
	}

}
