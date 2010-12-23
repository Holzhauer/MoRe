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
 * Created by Sascha Holzhauer on 08.11.2010
 */
package de.cesr.more.measures;

import de.cesr.more.measures.node.MNodeMeasureCategory;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 08.11.2010 
 *
 */
public enum MNetworkMeasureCategory implements MoreMeasureCategory{
	NOT_DEFINED("Not defined"),
	NETWORK_MISC("Network: Misc"),
	NETWORK_CENTRALITY("Network: Centrality"),
	NETWORK_CLUSTERING("Network: Clustering");

	private String desc;
	/**
	 * Constructs a new <code>MeasureCategory</code> by its description
	 * @param description
	 */
	private MNetworkMeasureCategory(String description) {
		desc = description;
	}
	
	/**
	 * Returns the description of this <code>MeasureCategory</code>
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return desc;
	}
	
	/**
	 * Test for equality by the category's description
	 * @date 15.08.2008
	 *
	 * @param category
	 * @return true if the categories have the same description
	 */
	public boolean equals(MNodeMeasureCategory category) {
		return desc == category.toString();
	}
}
