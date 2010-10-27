/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;



import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraEnvironment;
import de.cesr.lara.components.LaraSimpleAgent;
import de.cesr.lara.components.impl.AbstractLaraAgent;


/**
 * 
 * @author Sascha Holzhauer
 * @param <AgentT>
 *            the type (of agents) that are contained as nodes in the networks this agent refers to
 * @param <BOType> the type of behavioural options the BO-memory of this agent may store
 * @date 19.01.2010
 * 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractLaraNetworkAgent<AgentT extends LaraSimpleAgent, EdgeType, BOType extends LaraBehaviouralOption> 
	extends AbstractLaraAgent<AgentT, BOType> implements LaraSimpleNetworkAgent<AgentT, EdgeType> {

	LaraAgentNetworkComp<AgentT, EdgeType>	netComp;

	/**
	 * constructor
	 * 
	 * @param env
	 */
	public AbstractLaraNetworkAgent(LaraEnvironment env) {
		super(env);
		netComp = new LAgentNetworkComp<AgentT, EdgeType>(this);
	}

	/**
	 * constructor
	 * 
	 * @param env
	 * @param name
	 */
	public AbstractLaraNetworkAgent(LaraEnvironment env, String name) {
		super(env, name);
		netComp = new LAgentNetworkComp<AgentT, EdgeType>(this);
	}

	/**
	 * @see de.cesr.lara.components.LaraSimpleAgent#getLaraComp()
	 */
	public LaraAgentNetworkComp<AgentT, EdgeType> getLaraNetworkComp() {
		return netComp;
	}

	/**
	 * @see de.cesr.lara.components.LaraSimpleAgent#setLaraComp(de.cesr.lara.components.LaraAgentComponent)
	 */
	public void setLaraNetworkComp(LaraAgentNetworkComp<AgentT, EdgeType> component) {
		this.netComp = component;
	}
}
