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
 * Created by Sascha Holzhauer on 15.01.2010
 */
package de.cesr.more.measures.node;

/** 
 * Encapsulates information about average values of agent properties of
 * agents within a certain reach (i.d. number of links) from a given focal agent.
 * 
 * @author Sascha Holzhauer
 * @date 15.01.2010
 */
public interface MoreComboundNetworkInfo {
	
	/**
	 * @param value
	 */
	public void setValue(double value);
	
	/**
	 * @return value
	 */
	public double getValue();
	
	/**
	 * @return name
	 */
	public String getPropertyName();
	
	/**
	 * @return reach
	 */
	public int getReach();
}
