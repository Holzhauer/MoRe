/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;



import repast.simphony.context.space.graph.ContextJungNetwork;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraEnvironment;
import de.cesr.lara.components.LaraSimpleAgent;
import de.cesr.lara.components.impl.AbstractLaraAgent;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.util.MNodeMeasures;
import de.cesr.more.networks.MoreNetwork;



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
public abstract class AbstractLaraNetworkAgent<AgentT extends LaraSimpleAgent, EdgeType, BoType extends LaraBehaviouralOption> 
	extends AbstractLaraAgent<AgentT, BoType> implements LaraSimpleNetworkAgent<AgentT, EdgeType>, MoreNodeMeasureSupport {

	LaraAgentNetworkComp<AgentT, EdgeType>	netComp;
	MNodeMeasures measures = new MNodeMeasures();

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
	

	/**********************************************************
	 *** Network Measure Support ***
	 **********************************************************/

	/**
	 * @see edu.uos.sh.soneta.measures.NetworkMeasureSupport#getNetworkMeasureObject(repast.simphony.space.graph.JungNetwork,
	 *      edu.uos.sh.soneta.measures.NetworkMeasureUtilities.MeasureDescription)
	 */
	public Object getNetworkMeasureObject(
			MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
			MMeasureDescription key) {
		return measures.getNetworkMeasureObject(network, key);
	}

	/**
	 * @see edu.uos.sh.soneta.measures.NetworkMeasureSupport#setNetworkMeasureObject(repast.simphony.space.graph.JungNetwork,
	 *      edu.uos.sh.soneta.measures.NetworkMeasureUtilities.MeasureDescription,
	 *      java.lang.Object)
	 */
	public void setNetworkMeasureObject(
			MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
			MMeasureDescription key, Object value) {
		measures.setNetworkMeasureObject(network, key, value);
	}
}
