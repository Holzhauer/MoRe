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
import java.util.HashMap;
import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;

/** 
 * TODO check if networks of different type are required!
 * TODO make sure not more than one network with same name inserted
 * 
 * @author Sascha Holzhauer
 * @param <A> the common type (of agents) that is contained as nodes in the networks
 * @date 19.01.2010
 */
public class MLaraAgentNetworkComp<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>,
	E extends MoreEdge<? super A>> implements MoreLaraAgentNetworkComp<A, E> {
	
	MoreLaraNetworkAgent<A, E, BO> agent;
	

	/**
	 */
	private Map<String, MoreNetwork<A, E>> networks;
	
	
	/**
	 * @param agent
	 */
	public MLaraAgentNetworkComp(MoreLaraNetworkAgent<A, E, BO> agent) {
		super();
		this.agent = agent;
		networks = new HashMap<String, MoreNetwork<A, E>>();
	}


	/**
	 * @see de.cesr.more.lara.agent.MoreLaraAgentNetworkComp#perceiveNetworks()
	 */
	@Override
	public void perceiveNetworks() {
		// nothing to do
	}


	/**
	 * Getter of the property <tt>networks</tt>
	 * @return  Returns the networks.
	 */
	public Collection<MoreNetwork<A, E>> getNetworks() {
		return networks.values();
	}


	/**
	 * @see de.cesr.more.lara.agent.MoreLaraAgentNetworkComp#setNetwork(de.cesr.lara.components.LaraNetwork)
	 */
	@Override
	public void setNetwork(MoreNetwork<A, E> network) {
		this.networks.put(network.getName(), network);
	}
	

	/**
	 * @see de.cesr.more.lara.agent.MoreLaraAgentNetworkComp#getNetwork(java.lang.String)
	 */
	@Override
	public MoreNetwork<A, E> getNetwork(String name) {
		return this.networks.get(name);
	}
}
