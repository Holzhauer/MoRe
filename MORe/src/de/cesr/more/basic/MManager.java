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
 * Created by Sascha Holzhauer on 23.12.2010
 */
package de.cesr.more.basic;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 23.12.2010 
 *
 */
public class MManager {
	
	/**
	 * {@link NumberFormat} to format integer numbers
	 */
	protected static NumberFormat 		integerFormat;
	
	/**
	 * {@link NumberFormat} to format floating point numbers
	 */
	protected static NumberFormat 		floatPointFormat;
	
	
	/**
	 * @param format
	 * Created by Sascha Holzhauer on 23.12.2010
	 */
	public static void setFloatPointFormat(NumberFormat format) {
		floatPointFormat = format;
	}
	
	/**
	 * @param format
	 * Created by Sascha Holzhauer on 23.12.2010
	 */
	public static void setIntegerFormat(NumberFormat format) {
		integerFormat = format;
	}
	

	/**
	 * @return
	 * Created by Sascha Holzhauer on 23.12.2010
	 */
	public static NumberFormat getFloatPointFormat() {
		if (floatPointFormat == null) {
			floatPointFormat = new DecimalFormat("0.0000");
		}
		return floatPointFormat;
	}

	/**
	 * @return
	 * Created by Sascha Holzhauer on 23.12.2010
	 */
	public static NumberFormat getIntegerFormat() {
		if (integerFormat == null) {
			integerFormat = new DecimalFormat("000");
		}
		return integerFormat;
	}

}
