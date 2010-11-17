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
package de.cesr.more.rs.adapter;



import java.util.HashMap;
import java.util.Map;

import repast.simphony.engine.schedule.AbstractAction;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.measures.util.MoreSchedule;



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
		ISchedulableAction newAction = new AbstractAction(ScheduleParameters.createRepeating(params.getStart(), params
				.getInterval(), params.getPriority())) {

			@Override
			public void execute() {
				action.execute();
			}

		};
		actions.put(action, newAction);
		schedule.schedule(newAction);
	}

}
