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
 * @author Sascha Holzhauer
 * @date 15.11.2010 
 *
 */
public interface MoreSchedule {

	public void schedule(MScheduleParameters params, MoreAction action);
	
	public void removeAction(MoreAction action);
	
	/**
	 * Lists scheduled actions and according parameters in the returned string.
	 * @return
	 * Created by Sascha Holzhauer on 11.01.2011
	 */
	public String getScheduleInfo();
}
