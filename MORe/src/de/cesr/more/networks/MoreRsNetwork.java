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
 * Created by holzhauer on 12.10.2011
 */
package de.cesr.more.networks;

import de.cesr.more.edges.MoreEdge;
import de.cesr.more.exception.IllegalValueTypeException;
import de.cesr.more.measures.MMeasureDescription;
import repast.simphony.context.ContextListener;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.projection.Projection;

/**
 *
 * @author Sascha Holzhauer
 * 
 * EdgeT extends MRepastEdge<AgentT> does not use a wild card (? super AgentT) for compatibility reasons with Network<AgentT>
 * 
 * @param <AgentT> 
 * @param <EdgeT> 
 * @date 12.10.2010 
 *
 */
public interface MoreRsNetwork<AgentT, EdgeT extends RepastEdge<AgentT> & MoreEdge<AgentT>> 
	extends Projection<AgentT>, ContextListener<AgentT>, MoreNetwork<AgentT, EdgeT>, Network<AgentT> {
	
	/*************************************************
	 *  Accessing network measures by Repast Simphony
	 ************************************************/
	
	public double getMeasureA();
	
	public double getMeasureB();
	
	public void setMeasureA(MMeasureDescription desc) throws IllegalValueTypeException;
	
	public void setMeasureB(MMeasureDescription desc) throws IllegalValueTypeException;
}
