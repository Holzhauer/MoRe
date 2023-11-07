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
 * Created by Sascha Holzhauer on 10.04.2012
 */
package de.cesr.more.basic.edge;


import de.cesr.more.param.MDofNetworkPa;


/**
 * MORe
 * 
 * Interface for edges whose weight fade out. The amount of fading weight is determined by
 * {@link MDofNetworkPa#DYN_FADE_AMOUNT}, the interval of fades by {@link MDofNetworkPa#DYN_FADE_INTERVAL}
 * .
 * 
 * If {@link MDofNetworkPa#DYN_FADE_AMOUNT} is different than 0.0 the fading shall be scheduled automatically.
 * NOTE: Relevant is the value of {@link MDofNetworkPa#DYN_FADE_AMOUNT} at initialisation time of the edge
 * object!
 * 
 * @author Sascha Holzhauer
 * @date 10.04.2012
 * 
 */
public interface MoreFadingWeightEdge {

	/**
	 * Fades the edge weight by {@link MDofNetworkPa#DYN_FADE_AMOUNT}.
	 */
	public void fadeWeight();

}
