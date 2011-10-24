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
 * Created by Sascha Holzhauer on 24.01.2011
 */
package de.cesr.more.building.network;


/**
 * MORe
 * 
 * Used in conjunction with {@link MSmallWorldBetaModelNetworkGenerator}
 * @author Sascha Holzhauer
 * @param <V> the node type for that beta values are provided
 * @date 24.01.2011 
 *
 */
public interface MoreBetaProvider<V> {

	/**
	 * Returns the beta value to rewire in {@link MSmallWorldBetaModelNetworkGenerator}
	 * for the given node.
	 * 
	 * @param node
	 * @return
	 * Created by Sascha Holzhauer on 24.01.2011
	 */
	public double getBetaValue(V node );
}
