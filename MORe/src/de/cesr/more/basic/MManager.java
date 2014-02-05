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

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import cern.jet.random.engine.MersenneTwister;
import de.cesr.more.measures.network.MNetworkMeasureManager;
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

	/**
	 * Logger
	 */
	static private Logger				logger	= Logger.getLogger(MManager.class);

	protected static MoreSchedule schedule;

	protected static Context<Object>	rootContext;

	/**
	 * @return the rootContext
	 */
	public static Context<Object> getRootContext() {
		if (rootContext == null) {
			logger.error("The root context has not been set!");
			throw new IllegalStateException("The root context has not been set!");
		}
		return rootContext;
	}

	/**
	 * @param rootContext
	 *        the rootContext to set
	 */
	public static void setRootContext(Context<Object> rootContext) {
		MManager.rootContext = rootContext;
	}

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
		// init the general random stream:
		if (!PmParameterManager.isCustomised(MRandomPa.RND_STREAM) &&
				!getURandomService().isGeneratorRegistered((String) PmParameterManager.getParameter(
						MRandomPa.RND_STREAM))) {
			getURandomService().registerGenerator((String) PmParameterManager
					.getParameter(MRandomPa.RND_STREAM), new MersenneTwister(
							((Integer) PmParameterManager.getParameter(MRandomPa.RANDOM_SEED)).intValue()));
		}

		// init random streams if not done before, but only if random seed is customized:
		if (PmParameterManager.isCustomised(MRandomPa.RANDOM_SEED_NETWORK_BUILDING) &&
				(!PmParameterManager.isCustomised(MRandomPa.RND_STREAM_NETWORK_BUILDING) ||
				!getURandomService().isGeneratorRegistered((String) PmParameterManager.getParameter(
						MRandomPa.RND_STREAM_NETWORK_BUILDING)))) {

			PmParameterManager.setParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING,
					MRandomPa.RND_STREAM_NETWORK_BUILDING_CUSTOMISED);

			getURandomService().registerGenerator((String) PmParameterManager
					.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING), new MersenneTwister(
							((Integer) PmParameterManager.getParameter(MRandomPa.RANDOM_SEED_NETWORK_BUILDING))
									.intValue()));
		}
		
		// init random distribution if not done before:
		if (!PmParameterManager.isCustomised(MRandomPa.RND_UNIFORM_DIST_NETWORK_BUILDING) &&
				!getURandomService().isDistributionRegistered((String) PmParameterManager
						.getParameter(MRandomPa.RND_UNIFORM_DIST_NETWORK_BUILDING))) {
			getURandomService().registerDistribution(getURandomService().getNewUniformDistribution(
					getURandomService().getGenerator((String) PmParameterManager
									.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING))),
							(String) PmParameterManager
									.getParameter(MRandomPa.RND_UNIFORM_DIST_NETWORK_BUILDING));
		}

		// init random streams if not done before:
		if (PmParameterManager.isCustomised(MRandomPa.RANDOM_SEED_NETWORK_DYNAMICS) &&
				(!PmParameterManager.isCustomised(MRandomPa.RND_STREAM_NETWORK_DYNAMICS) ||
				!getURandomService().isGeneratorRegistered((String) PmParameterManager.getParameter(
						MRandomPa.RND_STREAM_NETWORK_DYNAMICS)))) {

			PmParameterManager.setParameter(MRandomPa.RND_STREAM_NETWORK_DYNAMICS,
					MRandomPa.RND_STREAM_NETWORK_DYNAMICS_CUSTOMISED);

			getURandomService().registerGenerator((String) PmParameterManager
					.getParameter(MRandomPa.RND_STREAM_NETWORK_DYNAMICS), new MersenneTwister(
							((Integer) PmParameterManager.getParameter(MRandomPa.RANDOM_SEED_NETWORK_DYNAMICS))
									.intValue()));
		}

		// init random distribution if not done before:
		if (!PmParameterManager.isCustomised(MRandomPa.RND_UNIFORM_DIST_NETWORK_DYNAMICS) &&
				!getURandomService().isDistributionRegistered((String) PmParameterManager
						.getParameter(MRandomPa.RND_UNIFORM_DIST_NETWORK_DYNAMICS))) {
			getURandomService().registerDistribution(getURandomService().getNewUniformDistribution(
					getURandomService().getGenerator((String) PmParameterManager
									.getParameter(MRandomPa.RND_STREAM_NETWORK_DYNAMICS))),
							(String) PmParameterManager
									.getParameter(MRandomPa.RND_UNIFORM_DIST_NETWORK_DYNAMICS));
		}
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
	
	public static void reset() {
		schedule = null;
		randomService = null;
		integerFormat = null;
		floatPointFormat = null;
		MNetworkManager.reset();
		MNetworkMeasureManager.reset();
	}
}
