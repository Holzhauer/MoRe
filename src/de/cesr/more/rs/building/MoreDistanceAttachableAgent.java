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
 * Created by Sascha Holzhauer on 25.11.2011
 */
package de.cesr.more.rs.building;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 25.11.2011 
 *
 */
public interface MoreDistanceAttachableAgent {

	/**
	 * Provides a weight regarding the given distance. I.e., some agents might prefer distant links, others might solely
	 * like local links. High weights promote agents that are the given distance away, low weights discriminate such
	 * agents.
	 * 
	 * Since such a preference may not only depend on the agent's milieu but also on its geographical embeddedness (in
	 * urban context the perception of distance is potentially different from rural contexts) the weight can be provided
	 * on an individual level instead by milieu preferences.
	 * 
	 * @param meanDistance
	 *        mean distance between all agents (by default only computed at initialisation of network service)
	 * @param distance
	 * @return the distance weight for the given distance
	 */
	public double getNetworkDistanceWeight(double meanDistance, double distance);
}