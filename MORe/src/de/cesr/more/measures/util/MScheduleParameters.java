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
 * Created by Sascha Holzhauer on 15.11.2010
 */
package de.cesr.more.measures.util;

/**
 * MORe
 *
 * Values are double because of Repast Simphony Scheduling
 * @author Sascha Holzhauer
 * @date 15.11.2010 
 *
 */
public class MScheduleParameters {
	
	// according to RS:
	public static final double RANDOM_PRIORITY = Double.NaN;
	public static final double FIRST_PRIORITY = Double.POSITIVE_INFINITY;
	public static final double LAST_PRIORITY = Double.NEGATIVE_INFINITY;
	  
	public static final double END_TICK = Double.POSITIVE_INFINITY;
	
	double start;
	double interval;
	double end;
	double priority;
	
	/**
	 * @return the start
	 */
	public double getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(double start) {
		this.start = start;
	}

	/**
	 * @return the interval
	 */
	public double getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(double interval) {
		this.interval = interval;
	}

	/**
	 * @return the end
	 */
	public double getPriority() {
		return priority;
	}

	/**
	 * @param priority the end to set
	 */
	public void setPriority(double priority) {
		this.priority = priority;
	}
	
	
	
	/**
	 * @return the end
	 */
	public double getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(double end) {
		this.end = end;
	}

	private MScheduleParameters(double start, double interval, double end, double priority) {
		this.start = start;
		this.interval = interval;
		this.end = end;
		this.priority = priority;
	}
	
	/**
	 * Priority is MScheduleParameters.RANDOM_PRIORITY.
	 * 
	 * @param start
	 * @param interval
	 * @param end 
	 * @param priority 
	 * @return
	 * Created by Sascha Holzhauer on 17.11.2010
	 */
	public static MScheduleParameters getScheduleParameter(double start, double interval, double end, double priority) {
		return new MScheduleParameters(start, interval, end, priority);
	}
	
	/**
	 * Priority is MScheduleParameters.RANDOM_PRIORITY.
	 * 
	 * @param start
	 * @param interval
	 * @return
	 * Created by Sascha Holzhauer on 17.11.2010
	 */
	public static MScheduleParameters getEverlastingRandomScheduleParameter(double start, double interval) {
		return new MScheduleParameters(start, interval, MScheduleParameters.END_TICK, MScheduleParameters.RANDOM_PRIORITY);
	}

	/**
	 * 
	 * Action starts right after Scheduling until [end] every [interval]th step.
	 * @param interval
	 * @param end
	 * @return
	 * Created by Sascha Holzhauer on 22.12.2010
	 */
	public static MScheduleParameters getFromBeginningRandomScheduleParameter(double interval, double end) {
		return new MScheduleParameters(0, interval, end, MScheduleParameters.RANDOM_PRIORITY);
	}

	/**
	 * Priority is MScheduleParameters.RANDOM_PRIORITY, start is 0;
	 * @param interval
	 * @return
	 * Created by Sascha Holzhauer on 17.11.2010
	 */
	public static MScheduleParameters getUnboundedRandomMScheduleParameters(double interval) {
		return new MScheduleParameters(0, interval, MScheduleParameters.END_TICK, MScheduleParameters.RANDOM_PRIORITY);
	}
	
	

}
