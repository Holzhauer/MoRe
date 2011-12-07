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
 * Created by holzhauer on 28.09.2011
 */
package de.cesr.more.rs.building;

import repast.simphony.context.Context;

/**
 * MORe
 * 
 * Used especially for RS network building
 *
 * @author holzhauer
 * @date 28.09.2011 
 *
 */
public interface MoreMilieuAgent {
	
	/**
	 * Return the milieu group index (starting with 1)
	 * @return
	 */
	public int getMilieuGroup();
	
	/**
	 * Mostly the group context this agent belongs to
	 * @return the parent context
	 */
	public Context<?> getParentContext();
	
	/**
	 * The agent's ID
	 * @return id
	 */
	public String getAgentId();

}
