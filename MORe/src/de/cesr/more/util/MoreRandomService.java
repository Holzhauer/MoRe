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

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 25.01.2011 
 *
 */
public interface MoreRandomService {
	/**
	 * Identifier for default uniform stream
	 */
	public final String UNIFORM_DEFAULT = "Uniform default";
	
	
	/**
	 * Identifier for default normal stream (not necessarily standard!)
	 */
	public final String NORMAL_DEFAULT = "Normal default"; 
	
	/**
	 * Return the default uniform distribution. Equivalent to
	 * <code>LaraRandom#getDistribution(MoreRandomServicem.UNIFORM_DEFAULT)</code>.
	 * 
	 * @return the default uniform distribution
	 */
	public Uniform getUniform();
	
	/**
	 * Returns a Uniform distribution that is fed by the given {@link RandomEngine}.
	 * Advantage: Uses a {@link MUniformDistributionController} in case logger is debug enabled.
	 * @param engine to fed the distribution
	 * @return uniform distribution
	 */
	public Uniform getNewUniformDistribution(RandomEngine engine);
	
	/**
	 * Create a normal distribution with the given parameters
	 * using the default random generator.
	 * 
	 * @param mean 
	 * @param stdDev
	 * @return normal distribution with the given parameters
	 */
	public Normal createNormal(double mean, double stdDev);
	
	/**
	 * Returns the {@link Normal} distribution that was created at last (therefore, 
	 * mean and standard deviation is not defined). Equivalent to
	 * <code>LaraRandom#getDistribution(MoreRandomService.NORMAL_DEFAULT)</code>.
	 * Returns null if no normal distribution has been created yet.
	 * 
 	 * @return latest created normal distribution
 	 * 
	 * Created by Sascha Holzhauer on 12.10.2010
	 */
	public Normal getNormal();
	
	/**
	 * Registers a custom {@link AbstractDistribution} at the given name.
	 * @param dist
	 * @param name
	 */
	public void registerDistribution(AbstractDistribution dist, String name);
	
	/**
	 * Returns the {@link AbstractDistribution} that is registered for the given name.
	 * @param name
	 * @return registered distribution
	 */
	public AbstractDistribution getDistribution(String name);
	
	/**
	 * Sets the seed for all random number generators. If this method is called,
	 * a new random number generator is created, all registered distributions (also customly
	 * registered) are deleted and the default distributions are renewed.
	 * 
	 * @param seed
	 */
	public void setSeed(int seed);
	
	/**
	 * Get the seed currently used for the default distributions.
	 * @return current seed
	 */
	public int getSeed();
	
}
