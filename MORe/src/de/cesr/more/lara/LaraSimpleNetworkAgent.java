/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;



import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.more.edges.MoreEdge;
import de.cesr.more.lara.LaraAgentNetworkComp;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;



/**
 * @author Sascha Holzhauer
 * @param <A> the common type (of agents) that is contained as nodes in the networks this agent refers to
 * @date 19.01.2010
 */
public interface LaraSimpleNetworkAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>, E extends MoreEdge<? super A>> extends LaraAgent<A, BO>,
	MoreNodeMeasureSupport{

	/**
	 * @return LARA Network Component
	 * @see de.cesr.lara.components.agents.LaraAgent#getLaraComp()
	 */
	public LaraAgentNetworkComp<A, E> getLaraNetworkComp();

	/**
	 * @param component 
	 * @see de.cesr.lara.components.agents.LaraAgent#setLaraComp(de.cesr.lara.components.LaraAgentComponent)
	 */
	public void setLaraNetworkComp(LaraAgentNetworkComp<A, E> component);

}
