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
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara.agent;



import java.util.Collection;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MNodeMeasures;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
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
public abstract class MAbstractLaraNetworkAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>, E extends MoreEdge<? super A>>
		extends LAbstractAgent<A, BO> implements MoreLaraNetworkAgent<A, E, BO>, MoreNodeMeasureSupport {

	MoreLaraAgentNetworkComp<A, E>	netComp;
	MNodeMeasures				measures	= new MNodeMeasures();

	/**
	 * Logger
	 */
	static private Logger		logger		= Log4jLogger.getLogger(MAbstractLaraNetworkAgent.class);

	/**
	 * constructor
	 * 
	 * @param env
	 */
	public MAbstractLaraNetworkAgent(LaraEnvironment env) {
		super(env);
		netComp = new MLaraAgentNetworkComp<A, BO, E>(this);
	}

	/**
	 * constructor
	 * 
	 * @param env
	 * @param name
	 */
	public MAbstractLaraNetworkAgent(LaraEnvironment env, String name) {
		super(env, name);
		netComp = new MLaraAgentNetworkComp<A, BO, E>(this);
	}

	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#getLaraComp()
	 */
	@Override
	public MoreLaraAgentNetworkComp<A, E> getLNetworkComp() {
		return netComp;
	}

	/**
	 * @see de.cesr.more.lara.agent.LaraSimpleNetworkAgent#setLaraNetworkComp(de.cesr.more.lara.agent.MoreLaraAgentNetworkComp)
	 */
	@Override
	public void setLNetworkComp(MoreLaraAgentNetworkComp<A, E> component) {
		this.netComp = component;
	}
	
	
	/**
	 * @see de.cesr.more.lara.agent.MoreLaraAgentNetworkComp#getNetwork(java.lang.String)
	 */
	@Override
	public MoreNetwork<A, E> getNetwork(String name) {
		return this.netComp.getNetwork(name);
	}
	
	/**
	 * @see de.cesr.more.lara.agent.MoreLaraAgentNetworkComp#setNetwork(de.cesr.more.basic.network.MoreNetwork)
	 */
	@Override
	public void setNetwork(MoreNetwork<A, E> network) {
		this.netComp.setNetwork(network);
	}
	
	/**
	 * @see de.cesr.more.lara.agent.MoreLaraAgentNetworkComp#getNetworks()
	 */
	@Override
	public Collection<MoreNetwork<A, E>> getNetworks() {
		return this.netComp.getNetworks();
	}

	/**********************************************************
	 *** Network Measure Support ***
	 **********************************************************/

	/**
	 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#getNetworkMeasureObject(de.cesr.more.basic.network.MoreNetwork,
	 * de.cesr.more.measures.MMeasureDescription)
	 */
	@Override
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
	 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#setNetworkMeasureObject(de.cesr.more.basic.network.MoreNetwork,
	 * de.cesr.more.measures.MMeasureDescription, java.lang.Number)
	 */
	@Override
	public void setNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
			MMeasureDescription key, Number value) {
		measures.setNetworkMeasureObject(network, key, value);
	}
}
