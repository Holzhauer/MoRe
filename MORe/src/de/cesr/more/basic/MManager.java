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

import de.cesr.lara.components.LaraRandom;
import de.cesr.more.measures.util.MoreSchedule;
import de.cesr.more.util.MRandom;
import de.cesr.more.util.MoreRandomService;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 23.12.2010
 * 
 */
public class MManager {

	protected static MoreSchedule		schedule;

	protected static MoreRandomService	randomService;

	/**
	 * {@link NumberFormat} to format integer numbers
	 */
	protected static NumberFormat		integerFormat;

	/**
	 * {@link NumberFormat} to format floating point numbers
	 */
	protected static NumberFormat		floatPointFormat;

	/**
	 * @param format Created by Sascha Holzhauer on 23.12.2010
	 */
	public static void setFloatPointFormat(NumberFormat format) {
		floatPointFormat = format;
	}

	/**
	 * @param format Created by Sascha Holzhauer on 23.12.2010
	 */
	public static void setIntegerFormat(NumberFormat format) {
		integerFormat = format;
	}

	/**
	 * @return Created by Sascha Holzhauer on 23.12.2010
	 */
	public static NumberFormat getFloatPointFormat() {
		if (floatPointFormat == null) {
			floatPointFormat = new DecimalFormat("0.0000");
		}
		return floatPointFormat;
	}

	/**
	 * @return Created by Sascha Holzhauer on 23.12.2010
	 */
	public static NumberFormat getIntegerFormat() {
		if (integerFormat == null) {
			integerFormat = new DecimalFormat("000");
		}
		return integerFormat;
	}

	/**
	 * @param schedule the schedule to set
	 */
	public static void setSchedule(MoreSchedule schedule) {
		MManager.schedule = schedule;
	}

	/**
	 * @return the schedule
	 */
	public static MoreSchedule getSchedule() {
		if (schedule == null) {
			throw new IllegalStateException("The MoreScheduler has not been set before!");
		}
		return MManager.schedule;
	}

	/**
	 * @return Created by Sascha Holzhauer on 11.01.2011
	 */
	public static boolean isScheduleSet() {
		return (schedule != null);
	}

	/**
	 * Return the random manager that is used for random processes in LARA. Either, the model author should implement
	 * (or assign to this.randomMan when extending AbstracLModel) the random number generator used in the custom model
	 * part, or reset the LRandom by calling getLRandom.setSeed(seed) using the correct seed parameter.
	 * 
	 * NOTE: Make sure that the {@link LaraRandom} class is instantiated only once since creating an instance every time
	 * this method is called results in starting the random sequence anew each time the method is called!
	 * 
	 * @return the random manager
	 */
	public static MoreRandomService getMRandomService() {
		if (randomService == null) {
			randomService = new MRandom(0);
		}
		return randomService;
	}
}
