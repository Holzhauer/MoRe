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
 * Created by Sascha Holzhauer on 17.11.2010
 */
package de.cesr.more.rs.util;



import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import repast.simphony.engine.schedule.AbstractAction;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import de.cesr.more.basic.MManager;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.measures.util.MoreSchedule;
import de.cesr.more.util.Log4jLogger;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 17.11.2010
 * 
 */
public class MRsSchedule implements MoreSchedule {

	ISchedule							schedule;
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MRsSchedule.class);

	/**
	 * Since RS identifies IActions by identity, theses objects need to be archived:
	 */
	Map<MoreAction, ISchedulableAction>	actions;

	public MRsSchedule(ISchedule schedule) {
		this.schedule = schedule;
		this.actions = new HashMap<MoreAction, ISchedulableAction>();
	}

	/**
	 * TODO error handling
	 * 
	 * @see de.cesr.more.measures.util.MoreSchedule#removeAction(de.cesr.more.measures.util.MoreAction)
	 */
	@Override
	public void removeAction(MoreAction action) {
		schedule.removeAction(actions.get(action));
		actions.remove(action);
	}

	/**
	 * 
	 * TODO test
	 * 
	 * @see de.cesr.more.measures.util.MoreSchedule#schedule(de.cesr.more.measures.util.MScheduleParameters,
	 *      de.cesr.more.measures.util.MoreAction)
	 */
	@Override
	public void schedule(MScheduleParameters params, final MoreAction action) {
		ScheduleParameters rSparams = ScheduleParameters.createRepeating(params.getStart(), params
				.getInterval(), params.getPriority());
		ISchedulableAction newAction = new AbstractAction(rSparams) {

			/**
			 * 
			 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void execute() {
				logger.debug("Execute action: " + action );
				action.execute();
			}

		};
		actions.put(action, newAction);
		logger.debug("Schedule MoreAction: " + action + "Parameter: " + params);
		schedule.schedule(rSparams, newAction);
		logger.debug("Scheduled IAction: " + newAction);
	}

	/**
	 * Returns a string containing all scheduled actions including their schedule parameters.
	 * @return information about scheduled actions
	 */
	@Override
	public String getScheduleInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("MSchedule Information:\n");
		for (Entry<MoreAction,ISchedulableAction> a : actions.entrySet()) {
			buffer.append(MManager.getFloatPointFormat().format(a.getValue().getNextTime()));
			buffer.append("\t: ");
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
		return schedule.getTickCount();
	}

}
