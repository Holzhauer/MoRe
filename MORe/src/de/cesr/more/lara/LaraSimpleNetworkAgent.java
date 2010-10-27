/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;



import de.cesr.lara.components.LaraSimpleAgent;
import de.cesr.more.lara.LaraAgentNetworkComp;



/**
 * @author Sascha Holzhauer
 * @param <AgentType> the common type (of agents) that is contained as nodes in the networks this agent refers to
 * @date 19.01.2010
 */
public interface LaraSimpleNetworkAgent<AgentType, EdgeType> extends LaraSimpleAgent {

	/**
	 * @return LARA Network Component
	 * @see de.cesr.lara.components.LaraSimpleAgent#getLaraComp()
	 */
	public LaraAgentNetworkComp<AgentType, EdgeType> getLaraNetworkComp();

	/**
	 * @param component 
	 * @see de.cesr.lara.components.LaraSimpleAgent#setLaraComp(de.cesr.lara.components.LaraAgentComponent)
	 */
	public void setLaraNetworkComp(LaraAgentNetworkComp<AgentType, EdgeType> component);

}
