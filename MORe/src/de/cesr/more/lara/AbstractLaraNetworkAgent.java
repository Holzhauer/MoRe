/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;



import repast.simphony.context.space.graph.ContextJungNetwork;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.util.MNodeMeasures;
import de.cesr.more.networks.MoreNetwork;



/**
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *            the type (of agents) that are contained as nodes in the networks this agent refers to
 * @param <BOType> the type of behavioural options the BO-memory of this agent may store
 * @date 19.01.2010
 * 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractLaraNetworkAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<A, BO>, E> 
	extends LAbstractAgent<A, BO> implements LaraSimpleNetworkAgent<A, BO, E>, MoreNodeMeasureSupport {

	LaraAgentNetworkComp<A, E>	netComp;
	MNodeMeasures measures = new MNodeMeasures();

	/**
	 * constructor
	 * 
	 * @param env
	 */
	public AbstractLaraNetworkAgent(LaraEnvironment env) {
		super(env);
		netComp = new LAgentNetworkComp<A, BO, E>(this);
	}

	/**
	 * constructor
	 * 
	 * @param env
	 * @param name
	 */
	public AbstractLaraNetworkAgent(LaraEnvironment env, String name) {
		super(env, name);
		netComp = new LAgentNetworkComp<A, BO, E>(this);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#getLaraComp()
	 */
	public LaraAgentNetworkComp<A, E> getLaraNetworkComp() {
		return netComp;
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#setLaraComp(de.cesr.lara.components.LaraAgentComponent)
	 */
	public void setLaraNetworkComp(LaraAgentNetworkComp<A, E> component) {
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
