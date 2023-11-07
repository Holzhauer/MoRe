/**
 * This file is part of
 * 
 * MORe - Managing Ongoing Relationships
 *
 * Copyright (C) 2010 Center for Environmental Systems Research, Kassel, Germany
 * 
 * Repast Simphony MySQL Database Outputter is free software: You can redistribute 
 * it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *  
 * Repast Simphony MySQL Database Outputter is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Center for Environmental Systems Research, Kassel
 * 
 * Created by Sascha Holzhauer on 28.10.2010
 */
package de.cesr.more.measures.util;

/**
 * MORe
 * 
 * An interface for actions objects that are intended to by scheduled and executed.
 *
 * @author Sascha Holzhauer
 * @date 28.10.2010 
 *
 */
public interface MoreAction {
	
	/**
	 * This method is executed - mostly automatically by some scheduler.
	 * 
	 * Created by Sascha Holzhauer on 28.10.2010
	 */
	public void execute();
}
