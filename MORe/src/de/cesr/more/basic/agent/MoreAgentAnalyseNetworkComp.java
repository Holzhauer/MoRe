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
 * Created by Sascha Holzhauer on 16.12.2011
 */
package de.cesr.more.basic.agent;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.rs.building.MoreDistanceAttachableAgent;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 16.12.2011 
 *
 */
public interface MoreAgentAnalyseNetworkComp<A, E extends MoreEdge<? super A>>
		extends MoreAgentNetworkComp<A, E>, MoreDistanceAttachableAgent {
	
	
	/**
	 * Returns the number of incoming links.
	 * 
	 * @return indegree
	 */
	public int getInDegree();
	
	/**
	 * Adds 1 to prevent 0 values for sizing in GIS
	 * 
	 * @return indegree + 1
	 */
	public int getXtInDegree();
	
	/**
	 * Returns the number of outgoing links.
	 * 
	 * @return outdegree
	 */
	public int getOutDegree();

	/**
	 * Calculates the average distance between this household and its neighbours.
	 * 
	 * @return average distance
	 */
	public float getNbrDispers();

	/**
	 * Returns the average in-degree of nearest neighbours (see Boguna2004)
	 * 
	 * @return average degree of nearest neighbours
	 */
	public float getNNAvgDeg();

	/**
	 * @return
	 */
	public double getNetPrefDev();


	/**
	 * @return
	 */
	public int getNetKDev();

	/**
	 * @param distance
	 * @param distance
	 * @return
	 */
	@Override
	public double getNetworkDistanceWeight(double meanDistance, double distance);
}
