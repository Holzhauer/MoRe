/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;



import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.util.MNodeMeasures;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.util.Log4jLogger;



/**
 * 
 * @author Sascha Holzhauer
 * @param <A> the type (of agents) that are contained as nodes in the networks this agent refers to
 * @param <BO> the type of behavioural options the BO-memory of this agent may store
 * @param <E> edge type
 * @date 19.01.2010
 * 
 */
public abstract class AbstractLaraNetworkAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>, E extends MoreEdge<? super A>>
		extends LAbstractAgent<A, BO> implements LaraSimpleNetworkAgent<A, BO, E>, MoreNodeMeasureSupport {

	LaraAgentNetworkComp<A, E>	netComp;
	MNodeMeasures				measures	= new MNodeMeasures();

	/**
	 * Logger
	 */
	static private Logger		logger		= Log4jLogger.getLogger(AbstractLaraNetworkAgent.class);

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
	 * @see de.cesr.more.lara.LaraSimpleNetworkAgent#setLaraNetworkComp(de.cesr.more.lara.LaraAgentNetworkComp)
	 */
	public void setLaraNetworkComp(LaraAgentNetworkComp<A, E> component) {
		this.netComp = component;
	}

	/**********************************************************
	 *** Network Measure Support ***
	 **********************************************************/

	/**
	 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#getNetworkMeasureObject(de.cesr.more.networks.MoreNetwork,
	 * de.cesr.more.measures.MMeasureDescription)
	 */
	public Number getNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
			MMeasureDescription key) {

		if (measures.getNetworkMeasureObject(network, key) == null) {
			// <- LOGGING
			logger.error("No mesure defined for key " + key);
			// LOGGING ->
		}
		return measures.getNetworkMeasureObject(network, key);
	}

	/**
	 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#setNetworkMeasureObject(de.cesr.more.networks.MoreNetwork,
	 * de.cesr.more.measures.MMeasureDescription, java.lang.Number)
	 */
	public void setNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
			MMeasureDescription key, Number value) {
		measures.setNetworkMeasureObject(network, key, value);
	}
}
