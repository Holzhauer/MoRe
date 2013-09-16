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

import org.apache.commons.math3.distribution.RealDistribution;


/**
 * MORe
 * 
 * Interface for instantiation and initialisation of continuous distributions to be used in a general manner.
 * 
 * @author Sascha Holzhauer
 * @date 14.09.2013
 * 
 */
public interface MRealDistribution extends RealDistribution {

	/**
	 * Set distribution specific parameters that are defined by a distribution specific enumeration that implements
	 * {@link MDistributionParameter}.
	 * 
	 * @param param
	 * @param a
	 */
	public void setParameter(MDistributionParameter param, double a);

	/**
	 * Initialises the distribution. Parameters need to be set before via
	 * {@link MRealDistribution#setParameter(MDistributionParameter, double)}!
	 */
	public void init();
}
