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
 * Created by Sascha Holzhauer on 16.12.2011
 */
package de.cesr.more.basic.agent;

import java.util.Collection;

import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.lara.agent.MAbstractLaraNetworkAgent;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MNodeMeasures;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.util.Log4jLogger;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 16.12.2011 
 *
 */
public abstract class MAbstractNetworkAgent<A, E extends MoreEdge<? super A>> implements MoreNetworkAgent<A, E>,
	MoreAgentNetworkComp<A, E>{

	protected MoreAgentNetworkComp<A, E>	netComp;
	protected MNodeMeasures					measures	= new MNodeMeasures();

	/**
	 * Logger
	 */
	static private Logger		logger		= Log4jLogger.getLogger(MAbstractLaraNetworkAgent.class);

	/**
	 * constructor
	 * 
	 * @param env
	 */
	public MAbstractNetworkAgent() {
		netComp = new MAgentNetworkComp<A, E>(getThis());
	}
	
	/**
	 * Must be implemented in subclasses when the agent type parameter gets concrete.
	 * 
	 * @return reference to this object of the agent parameter's type
	 */
	abstract public A getThis();

	/**
	 * @see de.cesr.lara.components.agents.LaraAgent#getLaraComp()
	 */
	@Override
	public MoreAgentNetworkComp<A, E> getNetworkComp() {
		return netComp;
	}

	/**
	 * @see de.cesr.more.lara.agent.LaraSimpleNetworkAgent#setLaraNetworkComp(de.cesr.more.basic.agent.MoreAgentNetworkComp)
	 */
	@Override
	public void setNetworkComp(MoreAgentNetworkComp<A, E> component) {
		this.netComp = component;
	}
	
	
	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#getNetwork(java.lang.String)
	 */
	@Override
	public MoreNetwork<A, E> getNetwork(String name) {
		return this.netComp.getNetwork(name);
	}
	
	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#setNetwork(de.cesr.more.basic.network.MoreNetwork)
	 */
	@Override
	public void setNetwork(MoreNetwork<A, E> network) {
		this.netComp.setNetwork(network);
	}
	
	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#getNetworks()
	 */
	@Override
	public Collection<MoreNetwork<A, E>> getNetworks() {
		return this.netComp.getNetworks();
	}
	


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#setMainNetwork(de.cesr.more.basic.network.MoreNetwork)
	 */
	@Override
	public void setMainNetwork(MoreNetwork<A, E> network) {
		getNetworkComp().setMainNetwork(network);
	}


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#getMainNetwork()
	 */
	@Override
	public MoreNetwork<A,E> getMainNetwork() {
		return getNetworkComp().getMainNetwork();
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
