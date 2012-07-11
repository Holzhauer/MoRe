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
 * Created by Sascha Holzhauer on 05.06.2012
 */
package de.cesr.more.manipulate.agent.analyse;

/**
 * MORe
 * 
 * Interface for agents that shall receive information about link manipulation.
 * 
 * @author Sascha Holzhauer
 * @date 05.06.2012 
 *
 */
public interface MoreLinkManipulationAnalysableAgent {
	
	
	/**
	 * @param counter
	 */
	public void setNumNewLinks(int counter);

	public void setNumNewTransitiveLinks(int counter);

	public void setNumNewReciprocalLinks(int counter);

	public void setNumNewLocalLinks(int counter);

	public void setNumPotTransitiveLinks(int counter);

	public void setNumPotReciprocalLinks(int counter);

	public void setNumPotLocalLinks(int counter);
}
