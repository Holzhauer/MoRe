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
 * Created by Sascha Holzhauer on 14.09.2013
 */
package de.cesr.more.util.distributions;


import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.PascalDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import de.cesr.more.rs.building.MGeoRsHomophilyDistanceFfNetworkService;


/**
 * MORe
 * 
 * Wrapper for {@link PascalDistribution} (also known as negative binomial distribution) to be used in e.g.
 * {@link MGeoRsHomophilyDistanceFfNetworkService}.
 * 
 * @author Sascha Holzhauer
 * @date 14.09.2013
 * 
 */
public class MPascalDistribution extends AbstractIntegerDistribution implements MIntegerDistribution {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6341906014724974550L;

	public enum MPascalDistributionParam implements MDistributionParameter {
		R,
		P;
	}

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MPascalDistribution.class);
	
	int					r;
	double				p;

	PascalDistribution	pascal;

	/**
	 * @param rng
	 * @param r
	 * @param p
	 * @throws NotStrictlyPositiveException
	 * @throws OutOfRangeException
	 */
	public MPascalDistribution(RandomGenerator rng) throws NotStrictlyPositiveException,
			OutOfRangeException {
		super(rng);
	}


	/**
	 * @see de.cesr.more.util.distributions.MRealDistribution#setParameter(de.cesr.more.util.distributions.MDistributionParameter, double)
	 */
	@Override
	public void setParameter(MDistributionParameter param, double value) {
		if (param.ordinal() == MPascalDistributionParam.R.ordinal()) {
			this.r = (int) value;
		} else if (param.ordinal() == MPascalDistributionParam.P.ordinal()) {
			this.p = value;
		} else {
			// <- LOGGING
			logger.warn("Given parameter (" + param + ") not defined for " + this);
			// LOGGING ->
		}
	}

	/**
	 * @see de.cesr.more.util.distributions.MRealDistribution#init()
	 */
	@Override
	public void init() {
		this.pascal = new PascalDistribution(this.random, this.r, this.p);
	}

	/**
	 * @see org.apache.commons.math3.distribution.PascalDistribution#probability(int)
	 */
	@Override
	public double probability(int x) {
		return this.pascal.probability(x);
	}

	/**
	 * @see org.apache.commons.math3.distribution.AbstractIntegerDistribution#cumulativeProbability(int, int)
	 */
	@Override
	public double cumulativeProbability(int x0, int x1) throws NumberIsTooLargeException {
		return this.pascal.cumulativeProbability(x0, x1);
	}

	/**
	 * @see org.apache.commons.math3.distribution.PascalDistribution#isSupportConnected()
	 */
	@Override
	public boolean isSupportConnected() {
		return this.pascal.isSupportConnected();
	}

	/**
	 * @see org.apache.commons.math3.distribution.PascalDistribution#cumulativeProbability(int)
	 */
	@Override
	public double cumulativeProbability(int x) {
		return this.pascal.cumulativeProbability(x);
	}


	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#getNumericalMean()
	 */
	@Override
	public double getNumericalMean() {
		return this.pascal.getNumericalMean();
	}


	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#getNumericalVariance()
	 */
	@Override
	public double getNumericalVariance() {
		return this.pascal.getNumericalVariance();
	}


	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#getSupportLowerBound()
	 */
	@Override
	public int getSupportLowerBound() {
		return this.pascal.getSupportLowerBound();
	}


	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#getSupportUpperBound()
	 */
	@Override
	public int getSupportUpperBound() {
		return this.pascal.getSupportUpperBound();
	}
}
