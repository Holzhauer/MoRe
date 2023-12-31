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
 * Created by Sascha Holzhauer on 21.04.2011
 */
package de.cesr.more.manipulate.agent;

/**
 * MORe
 *
 * Interface for agent classes that shall use a link manipulation processor like
 * {@link MThresholdLinkProcessor}
 * 
 * @author Sascha Holzhauer
 * @date 21.04.2011 
 *
 */
public interface MoreLinkManipulatableAgent<A> {

	
	/**
	 * Get the difference in a certain value to another agent from this agent.
	 * @param agent
	 * @return
	 */
	public double getValueDifference(A agent);
}
