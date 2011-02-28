/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;



import java.util.Collection;

import de.cesr.lara.components.LaraAgent;
import de.cesr.lara.components.LaraAgentComponent;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.more.networks.MoreNetwork;



/**
 * Interface for agents with network support that allows to directly call the {@link LaraAgentComponent} and
 * {@link LaraAgentNetworkComp} methods at the agent.
 * 
 * @author Sascha Holzhauer
 * @param <A> the common type (of agents) that is contained as nodes in the networks this agent refers to
 * @param <E> edge type
 * @param <BO> behavioural option type
 * @date 19.01.2010
 */
public interface LaraNetworkAgent<A extends LaraAgent<A, BO>, E, BO extends LaraBehaviouralOption<? super A, BO>>
		extends LaraAgent<A, BO>, LaraAgentNetworkComp<A, E> {

	/**
	 * @return Set of LaraNetworks
	 */
	public abstract Collection<MoreNetwork<? super A, E>> getLNetworks();

	/**
	 * @return LARA Network Component
	 */
	public abstract LaraAgentNetworkComp<LaraNetworkAgent<A, E, BO>, E> getLNetworkComp();

}
