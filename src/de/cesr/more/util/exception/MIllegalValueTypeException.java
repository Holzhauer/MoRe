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
package de.cesr.more.util.exception;

import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.MoreMeasure;

/**
 * MORe
 * Used for specifying {@link MMeasureDescription}s that represent a {@link MoreMeasure} with a certain value type.
 * 
 * @author Sascha Holzhauer
 * @date 17.11.2010 
 *
 */
public class MIllegalValueTypeException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -164108977908903828L;

	public MIllegalValueTypeException(String message) {
		super(message);
	}

}
