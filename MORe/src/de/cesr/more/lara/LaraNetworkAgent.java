/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;



import java.util.Collection;

import de.cesr.lara.components.LaraSimpleAgent;
import de.cesr.lara.components.LaraAgentComponent;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.more.lara.LaraAgentNetworkComp;
import de.cesr.more.lara.LaraNetworkAgent;
import de.cesr.more.networks.MoreNetwork;
import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter.EdgeType;



/**
 * Interface for agents with network support that allows to directly call the {@link LaraAgentComponent} and {@link LaraAgentNetworkComp} methods at
 * the agent.
 * 
 * @author Sascha Holzhauer
 * @param <AgentT> the common type (of agents) that is contained as nodes in the networks this agent refers to
 * @date 19.01.2010
 */
public interface LaraNetworkAgent<AgentT extends LaraSimpleAgent, EdgeType, BoType extends LaraBehaviouralOption<?>> 
	extends LaraSimpleAgent, LaraAgentNetworkComp<AgentT, EdgeType> {

	/**
	 * @return Set of LaraNetworks
		 */
	public abstract Collection<MoreNetwork<LaraSimpleAgent, EdgeType>> getLNetworks();

	/**
	 * @return LARA Network Component
			 */
	public abstract LaraAgentNetworkComp<LaraNetworkAgent<AgentT, EdgeType, BoType>, EdgeType> getLNetworkComp();

}
