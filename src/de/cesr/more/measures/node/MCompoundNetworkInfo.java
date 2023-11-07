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
 * @author Sascha Holzhauer
 * @date 15.01.2010
 */
public class MCompoundNetworkInfo implements MoreComboundNetworkInfo {

	
	/**
	 * @param name
	 * @param reach
	 */
	public MCompoundNetworkInfo(String name, int reach) {
		this.propertyName = name;
		this.reach = reach;
	}
	
	private int		reach;

	private String	propertyName;

	private double	value;

	/**
	 * Getter of the property <tt>reach</tt>
	 */
	@Override
	public int getReach() {
		return reach;
	}

	/**
	 * Setter of the property <tt>reach</tt>
	 * @param reach 
	 */
	public void setReach(int reach) {
		this.reach = reach;
	}

	/**
	 * @see de.cesr.more.measures.node.MoreComboundNetworkInfo#getPropertyName()
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Setter of the property <tt>propertyName</tt>
	 * 
	 * @param propertyName
	 *            The propertyName to set.
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @see de.cesr.more.measures.node.MoreComboundNetworkInfo#getValue()
	 */
	@Override
	public double getValue() {
		return value;
	}

	/**
	 * @see de.cesr.more.measures.node.MoreComboundNetworkInfo#setValue(double)
	 */
	@Override
	public void setValue(double value) {
		this.value = value;
	}
}
