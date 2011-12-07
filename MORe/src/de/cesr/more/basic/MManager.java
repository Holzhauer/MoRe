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
 * Created by Sascha Holzhauer on 23.12.2010
 */
package de.cesr.more.basic;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;

import de.cesr.more.measures.util.MoreSchedule;
import de.cesr.more.param.MRandomPa;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.uranus.core.URandomService;
import de.cesr.uranus.core.UranusRandomService;

/**
 * MORe Manager
 * 
 * @author Sascha Holzhauer
 * @date 23.12.2010
 * 
 */
public class MManager {

	protected static MoreSchedule schedule;

	protected static UranusRandomService randomService;

	/**
	 * {@link NumberFormat} to format integer numbers
	 */
	protected static NumberFormat integerFormat;

	/**
	 * {@link NumberFormat} to format floating point numbers
	 */
	protected static NumberFormat floatPointFormat;

	/**
	 * Initialised parameter framework.
	 */
	public static void init() {
		// init random streams
		getURandomService()
				.registerDistribution(
						new Uniform(
								new MersenneTwister(
										((Integer) PmParameterManager
												.getParameter(MRandomPa.RANDOM_SEED_NETWORK_BUILDING))
												.intValue())),
						(String) PmParameterManager
								.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING));
	}

	/**
	 * @param format
	 */
	public static void setFloatPointFormat(NumberFormat format) {
		floatPointFormat = format;
	}

	/**
	 * @param the
	 *            common format for integers
	 */
	public static void setIntegerFormat(NumberFormat format) {
		integerFormat = format;
	}

	/**
	 * Mainly used for logging purposes.
	 * 
	 * @return common format for float numbers
	 */
	public static NumberFormat getFloatPointFormat() {
		if (floatPointFormat == null) {
			floatPointFormat = new DecimalFormat("0.0000");
		}
		return floatPointFormat;
	}

	/**
	 * Mainly used for logging purposes.
	 * 
	 * @return common format for integers
	 */
	public static NumberFormat getIntegerFormat() {
		if (integerFormat == null) {
			integerFormat = new DecimalFormat("000");
		}
		return integerFormat;
	}

	/**
	 * @param schedule
	 *            the schedule to set
	 */
	public static void setSchedule(MoreSchedule schedule) {
		MManager.schedule = schedule;
	}

	/**
	 * Used for instance to schedule network measure calculations.
	 * 
	 * @return the schedule
	 */
	public static MoreSchedule getSchedule() {
		if (schedule == null) {
			throw new IllegalStateException(
					"The MoreScheduler has not been set before!");
		}
		return MManager.schedule;
	}

	/**
	 * @return true if the schedule has been set
	 */
	public static boolean isScheduleSet() {
		return (schedule != null);
	}

	/**
	 * Return the random manager that is used for random processes.
	 * 
	 * @return the random manager
	 */
	public static UranusRandomService getURandomService() {
		if (randomService == null) {
			randomService = new URandomService(
					((Integer) PmParameterManager
							.getParameter(MRandomPa.RANDOM_SEED)).intValue());
		}
		return randomService;
	}
}
