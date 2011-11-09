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
 * Created by Sascha Holzhauer on 10.12.2010
 */
package de.cesr.more.util;



import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.cesr.more.basic.MManager;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.measures.util.MoreSchedule;



/**
 * MORe
 * 
 * Simple and inefficient stand-alone schedule (mainly for testing purposes).
 * Does not consider priorities!
 * Later schedulings overwrite older one for the same action!
 * 
 * @author Sascha Holzhauer
 * @date 10.12.2010
 * 
 */
public class MSchedule implements MoreSchedule {

	private Map<MoreAction, MScheduleParameters>	actions	= new HashMap<MoreAction, MScheduleParameters>();
	
	protected double currentStep;
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MSchedule.class);

	/**
	 * @see de.cesr.more.measures.util.MoreSchedule#removeAction(de.cesr.more.measures.util.MoreAction)
	 */
	@Override
	public void removeAction(MoreAction action) {
		actions.remove(action);
	}

	/**
	 * @see de.cesr.more.measures.util.MoreSchedule#schedule(de.cesr.more.measures.util.MScheduleParameters,
	 *      de.cesr.more.measures.util.MoreAction)
	 */
	@Override
	public void schedule(MScheduleParameters params, MoreAction action) {
		actions.put(action, params);
	}

	/**
	 * 
	 * Execute actions scheduled for the given step.
	 * 
	 * @param step
	 */
	public void step(double step) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(this.getScheduleInfo());
		}
		// LOGGING ->
		currentStep = step;

		for (MoreAction a : actions.keySet()) {
			if (actions.get(a).getStart() <= step && actions.get(a).getEnd() >= step) {
				if ((step - actions.get(a).getStart()) % actions.get(a).getInterval() == 0) {
					a.execute();
				}
			}
		}
	}
	
	/**
	 * Checks whether the given {@link MoreAction} is scheduled for the given timestep.
	 * @param action
	 * @param timestep
	 * @return
	 * Created by Sascha Holzhauer on 22.12.2010
	 */
	public boolean isScheduled(MoreAction action, int timestep) {
		if (! actions.containsKey(action)) {
			return false;
		}
		if (actions.get(action).getStart() > timestep) {
			return false;
		}
		if (actions.get(action).getEnd() < timestep) {
			return false;
		}
		if ((timestep - actions.get(action).getStart()) % actions.get(action).getInterval() != 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns a string containing all scheduled actions including their schedule parameters.
	 * @return information about scheduled actions
	 * 
	 * Created by Sascha Holzhauer on 23.12.2010
	 */
	@Override
	public String getScheduleInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("MSchedule (" + toString() + ") Information:\n");
		for (Entry<MoreAction, MScheduleParameters> a : actions.entrySet()) {
			buffer.append(MManager.getFloatPointFormat().format(a.getValue().getStart()));
			buffer.append("\t> ");
			buffer.append(MManager.getFloatPointFormat().format(a.getValue().getInterval()));
			buffer.append("\t> ");
			buffer.append(a.getValue().getEnd());
			buffer.append("\t ");
			buffer.append(a.getKey());
			buffer.append("\n");
		}
		return buffer.toString();
	}

	/**
	 * @see de.cesr.more.measures.util.MoreSchedule#getCurrentTick()
	 */
	@Override
	public double getCurrentTick() {
		return currentStep;
	}
}
