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
 * Created by holzhauer on 15.01.2009
 */
package de.cesr.more.util;


import org.apache.commons.collections15.bidimap.DualHashBidiMap;


/**
 * MORe
 * 
 * @author holzhauer
 * @date 15.01.2009
 * 
 *       Defines the indices for milieus used in database and java code. In contrast to an enumeration this
 *       implementation enables edition of indices when reading DB.
 * 
 *       Default map for milieu identifiers
 */
public class MDefaultMilieuKeysMap extends DualHashBidiMap<String, Integer> {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public MDefaultMilieuKeysMap() {
		int id = 0;
		this.put("GLM", new Integer(++id));
		this.put("TRA", new Integer(++id));
		this.put("MAI", new Integer(++id));
		this.put("HED", new Integer(++id));
	}
}
