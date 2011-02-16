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
 * Created by Sascha Holzhauer on 25.01.2011
 */
package de.cesr.more.util;



import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import de.cesr.lara.components.impl.Log4jLogger;
import de.cesr.lara.components.impl.util.UniformController;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 25.01.2011
 * 
 */
public class MRandom implements MoreRandomService {
	/**
	 * Logger
	 */
	static private Logger						logger	= Log4jLogger.getLogger(MRandom.class);

	private Map<String, AbstractDistribution>	distributions;

	private RandomEngine						defaultGenerator;

	private int									seed;

	/**
	 * Initialise a new instance with the given random seed.
	 * 
	 * @param seed
	 */
	public MRandom(int seed) {
		this.seed = seed;
		defaultGenerator = new MersenneTwister(seed);
		distributions = new HashMap<String, AbstractDistribution>();

		if (logger.isDebugEnabled()) {
			distributions.put(UNIFORM_DEFAULT, new UniformController(defaultGenerator));
		} else {
			distributions.put(UNIFORM_DEFAULT, new Uniform(defaultGenerator));
		}
	}

	/**
	 * @see de.cesr.more.util.MoreRandomService#getDistribution(java.lang.String)
	 */
	@Override
	public AbstractDistribution getDistribution(String name) {
		return distributions.get(name);
	}

	/**
	 * @see de.cesr.more.util.MoreRandomService#getNormal()
	 */
	@Override
	public Normal getNormal() {
		if (!distributions.containsKey(NORMAL_DEFAULT)) {
			logger.warn("Normal distributions has not been created!");
		}
		return (Normal) distributions.get(NORMAL_DEFAULT);
	}

	/**
	 * @see de.cesr.more.util.MoreRandomService#createNormal(double, double)
	 */
	public Normal createNormal(double mean, double std) {
		distributions.put(NORMAL_DEFAULT, new Normal(mean, std, defaultGenerator));
		return (Normal) distributions.get(NORMAL_DEFAULT);
	}

	/**
	 * @see de.cesr.more.util.MoreRandomService#getUniform()
	 */
	@Override
	public Uniform getUniform() {
		return (Uniform) distributions.get(UNIFORM_DEFAULT);
	}

	/**
	 * @see de.cesr.more.util.MoreRandomService#registerDistribution(cern.jet.random.AbstractDistribution,
	 *      java.lang.String)
	 */
	public void registerDistribution(AbstractDistribution dist, String name) {
		distributions.put(name, dist);
	}

	/**
	 * @see de.cesr.more.util.MoreRandomService#setSeed(int)
	 */
	public void setSeed(int seed) {
		this.seed = seed;
		invalidateDistributions();
		defaultGenerator = new MersenneTwister(seed);
		if (logger.isDebugEnabled()) {
			distributions.put(UNIFORM_DEFAULT, new UniformController(defaultGenerator));
		} else {
			distributions.put(UNIFORM_DEFAULT, new Uniform(defaultGenerator));
		}

	}

	private void invalidateDistributions() {
		distributions.clear();
	}

	/**
	 * @see de.cesr.more.util.MoreRandomService#getSeed()
	 */
	@Override
	public int getSeed() {
		return this.seed;
	}
}
