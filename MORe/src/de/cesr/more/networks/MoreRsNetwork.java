/**
 * KUBUS_Proto01
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 12.10.2010
 */
package de.cesr.more.networks;

import repast.simphony.context.ContextListener;
import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.projection.Projection;

/**
 *
 * @author Sascha Holzhauer
 * 
 * @param <AgentT> 
 * @param <EdgeT> 
 * @date 12.10.2010 
 *
 */
public interface MoreRsNetwork<AgentT, EdgeT extends RepastEdge<AgentT>> extends Projection<AgentT>, ContextListener<AgentT>, 
	MoreNetwork<AgentT, EdgeT>, Network<AgentT> {

	/**
	 * @param edgeCreator
	 * Created by Sascha Holzhauer on 12.10.2010
	 */
	public void setEdgeFactory(EdgeCreator<? extends RepastEdge, AgentT> edgeCreator);
}
