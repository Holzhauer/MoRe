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
 * Created by Sascha Holzhauer on 03.12.2010
 */
package de.cesr.more.basic.edge;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 03.12.2010 
 *
 */
public interface MoreEdge<AgentType> {

	/**
	 * For undirected edges, it returns the node that was given as first node parameter.
	 * @return the edge's start node
	 */
	public AgentType getStart();
	
	/**
	 * For undirected edges, it returns the node that was given as second node parameter.
	 * @return the edge's target node
	 */
	public AgentType getEnd();
	
	/**
	 * @return the weight that is associated with this edge
	 */
	public double getWeight();
	
	/**
	 * @param weight that is associated with this edge
	 */
	public void setWeight(double weight);
	
	/**
	 * @return true if this edge is directed
	 */
	public boolean isDirected();
}
