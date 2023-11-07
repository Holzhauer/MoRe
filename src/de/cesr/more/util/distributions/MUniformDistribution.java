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
 * Created by sholzhau on 7 Jun 2014
 */
package de.cesr.more.util.distributions;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;


/**
 * MORe
 *
 * @author sholzhau
 * @date 7 Jun 2014
 *
 */
public class MUniformDistribution extends AbstractIntegerDistribution implements MIntegerDistribution{

	/**
	 *
	 */
	private static final long	serialVersionUID	= -4673402817901686272L;

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MUniformDistribution.class);

	public enum MUniformDistributionParam implements MDistributionParameter {
		L,
		U;
	}

	int l,u;

	UniformIntegerDistribution uniform;

	public MUniformDistribution(RandomGenerator rng) throws NotStrictlyPositiveException, OutOfRangeException {
		super(rng);
	}

	/**
	 * @see de.cesr.more.util.distributions.MRealDistribution#setParameter(de.cesr.more.util.distributions.MDistributionParameter, double)
	 */
	@Override
	public void setParameter(MDistributionParameter param, double value) {
		if (param.ordinal() == MUniformDistributionParam.L.ordinal()) {
			this.l = (int) value;
		} else if (param.ordinal() == MUniformDistributionParam.U.ordinal()) {
			this.u = (int) value;
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
		this.uniform = new UniformIntegerDistribution(this.random, this.l, this.u);
	}

	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#cumulativeProbability(int)
	 */
	@Override
	public double cumulativeProbability(int x) {
		return this.uniform.cumulativeProbability(x);
	}

	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#getNumericalMean()
	 */
	@Override
	public double getNumericalMean() {
		return this.uniform.getNumericalMean();
	}

	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#getNumericalVariance()
	 */
	@Override
	public double getNumericalVariance() {
		return this.uniform.getNumericalVariance();
	}

	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#getSupportLowerBound()
	 */
	@Override
	public int getSupportLowerBound() {
		return this.uniform.getSupportLowerBound();
	}

	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#getSupportUpperBound()
	 */
	@Override
	public int getSupportUpperBound() {
		return this.uniform.getSupportUpperBound();
	}

	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#isSupportConnected()
	 */
	@Override
	public boolean isSupportConnected() {
		return this.uniform.isSupportConnected();
	}

	/**
	 * @see org.apache.commons.math3.distribution.IntegerDistribution#probability(int)
	 */
	@Override
	public double probability(int x) {
		return this.uniform.probability(x);
	}
}
